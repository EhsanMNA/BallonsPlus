package be.shark_zekrom.commands;

import be.shark_zekrom.Main;
import be.shark_zekrom.Storage;
import be.shark_zekrom.inventory.Menu;
import be.shark_zekrom.utils.Balloon;
import be.shark_zekrom.utils.GetSkull;
import be.shark_zekrom.utils.SummonBalloons;
import be.shark_zekrom.utils.economy.Economy;
import be.shark_zekrom.utils.permission.PermissionServices;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;

public class Balloons implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {

        String prefix = "§bBalloons §f» ";

        Player player = (Player) sender;
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                player.sendMessage(ChatColor.AQUA + "");
                player.sendMessage(ChatColor.AQUA + "/balloons help");
                player.sendMessage(ChatColor.AQUA + "/balloons menu");
                player.sendMessage(ChatColor.AQUA + "/balloons spawn <name>");
                player.sendMessage(ChatColor.AQUA + "/balloons remove");
                player.sendMessage(ChatColor.AQUA + "/balloons particles <on/off>");
                player.sendMessage(ChatColor.AQUA + "");
            }
            if (args[0].equalsIgnoreCase("remove")) {
                if (SummonBalloons.balloons.containsKey(player)) {
                    SummonBalloons.removeBalloon(player);
                    SummonBalloons.playerBalloons.remove(player);

                    player.playSound(player.getLocation(),Storage.removeSound, 10,2);
                    player.sendMessage(prefix + Main.getInstance().getConfig().getString("BalloonsRemoved"));
                }
            }
            if (args[0].equalsIgnoreCase("menu")) {
                if (player.isInsideVehicle()) {
                    player.sendMessage(prefix + Main.getInstance().getConfig().getString("CantOpenInventory"));
                } else {
                    Menu.inventory(player, 0);
                }
            }
            if (args[0].equalsIgnoreCase("reload")) {
                Menu.list.clear();
                ConfigurationSection cs = Main.getInstance().getConfig().getConfigurationSection("Balloons");
                Menu.list.addAll(cs.getKeys(false));

                Storage.removeSound = Sound.valueOf(Main.getInstance().getConfig().getString("BalloonsRemovedSound"));
                Storage.summonSound = Sound.valueOf(Main.getInstance().getConfig().getString("BalloonsSummonedSound"));
                Storage.particleToggleSound = Sound.valueOf(Main.getInstance().getConfig().getString("BalloonsParticleToggleSound"));
                Storage.showOnlyBalloonsWithPermission = Main.getInstance().getConfig().getBoolean("ShowOnlyBalloonsWithPermission");
                player.sendMessage(prefix + "reloaded.");

            }
        } else if (args.length > 1) {
            if (args[0].equalsIgnoreCase("spawn")) {
                File file = new File(Main.getInstance().getDataFolder(), "config.yml");
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                if (config.getString("Balloons." + args[1]) != null) {

                    String permission = config.getString("Balloons." + args[1] + ".permission");
                    if (sender.hasPermission(permission)) {
                        if (SummonBalloons.balloons.containsKey(player)) {
                            if (config.getString("Balloons." + args[1] + ".item") != null) {
                                ItemStack itemStack = new ItemStack(Material.valueOf(config.getString("Balloons." + args[1] + ".item")));
                                ItemMeta itemMeta = itemStack.getItemMeta();
                                itemMeta.setCustomModelData(config.getInt("Balloons." + args[1] + ".custommodeldata"));
                                itemStack.setItemMeta(itemMeta);
                                SummonBalloons.as.get(player).getEquipment().setHelmet(itemStack);
                            } else {
                                SummonBalloons.as.get(player).getEquipment().setHelmet(GetSkull.createSkull(config.getString("Balloons." + args[1] + ".head")));
                            }
                        } else {
                            if (config.getString("Balloons." + args[1] + ".item") != null) {
                                ItemStack itemStack = new ItemStack(Material.valueOf(config.getString("Balloons." + args[1] + ".item")));
                                ItemMeta itemMeta = itemStack.getItemMeta();
                                itemMeta.setCustomModelData(config.getInt("Balloons." + args[1] + ".custommodeldata"));
                                itemStack.setItemMeta(itemMeta);
                                SummonBalloons.summonBalloon(player, itemStack);
                            } else {
                                SummonBalloons.summonBalloon(player, GetSkull.createSkull(config.getString("Balloons." + args[1] + ".head")));
                            }
                        }
                        player.playSound(player.getLocation(),Storage.summonSound,10,2);
                        SummonBalloons.playerBalloons.put(player, args[1]);
                        player.playSound(player.getLocation(),Storage.summonSound, 10,2);
                        player.sendMessage(prefix + Main.getInstance().getConfig().getString("BalloonsSummoned"));

                    } else {
                        player.sendMessage(prefix + Main.getInstance().getConfig().getString("NoBalloonsPermission"));
                    }
                } else {
                    player.sendMessage(prefix + Main.getInstance().getConfig().getString("NoBalloonsFound"));
                }
            }
            if (args[0].equalsIgnoreCase("particles")){
                if (args[1].equalsIgnoreCase("on")){
                    Storage.disabledBalloonParticles.remove(player.getUniqueId());
                    player.sendMessage(prefix + ChatColor.GREEN + "Balloons particles enabled.");
                }
                if (args[1].equalsIgnoreCase("off")){
                    Storage.disabledBalloonParticles.add(player.getUniqueId());
                    player.sendMessage(prefix + ChatColor.RED + "Balloons particles disabled.");
                }
                player.playSound(player.getLocation(),Storage.particleToggleSound, 10,2);
            }
            if (args[0].equalsIgnoreCase("buy")){
                String ball = args[1];
                if (Menu.list.contains(ball)){
                    for (Balloon balloon : Menu.balloons){
                        if (balloon.getName().equalsIgnoreCase(ball)){
                            Economy eco = Storage.getEconomy();
                            if (eco.getMoney(player) >= balloon.getPrice()){
                                eco.removeMoney(player, balloon.getPrice());
                            }else {
                                player.sendMessage(prefix + ChatColor.RED + "You don't have enough money!");
                                player.playSound(player.getLocation(),Sound.BLOCK_GLASS_BREAK,10,2);
                                return false;
                            }
                            if (Storage.hasPermissionServices){
                                String permission = balloon.getPermission();
                                PermissionServices permissionServices = Storage.permissionServices;
                                permissionServices.addPermission(player,permission);
                            }
                            player.sendMessage(prefix + ChatColor.GREEN + "You bought " + balloon.getName() + " balloon!");
                            player.playSound(player.getLocation(),Sound.ENTITY_EVOKER_DEATH, 10,2);
                        }
                    }
                }
                else {
                    player.sendMessage(prefix + ChatColor.RED + "This balloon is not exists!");
                    player.playSound(player.getLocation(),Sound.BLOCK_GLASS_BREAK,10,2);
                }
            }
        } else {
            player.sendMessage("");
            player.sendMessage(ChatColor.AQUA + "/balloons help");
            player.sendMessage(ChatColor.AQUA + "/balloons menu");
            player.sendMessage(ChatColor.AQUA + "/balloons spawn <name>");
            player.sendMessage(ChatColor.AQUA + "/balloons buy <name>");
            player.sendMessage(ChatColor.AQUA + "/balloons remove");
            player.sendMessage(ChatColor.AQUA + "/balloons particles <on/off>");
            player.sendMessage(ChatColor.AQUA + "");
        }
        return false;
    }


}