package be.shark_zekrom;

import be.shark_zekrom.commands.Balloons;
import be.shark_zekrom.commands.BalloonsTabCompleter;
import be.shark_zekrom.inventory.Menu;
import be.shark_zekrom.utils.Balloon;
import be.shark_zekrom.utils.Distance;
import be.shark_zekrom.utils.GetSkull;
import be.shark_zekrom.listener.Listener;
import be.shark_zekrom.utils.SummonBalloons;
import be.shark_zekrom.utils.economy.Economy;
import be.shark_zekrom.utils.economy.VaultEconomy;
import be.shark_zekrom.utils.permission.VaultPermission;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

public class Main extends JavaPlugin {

    private static Main instance;
    public static Main getInstance() {
        return instance;
    }


    @Override
    public void onEnable() {
        instance = this;
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new Listener(), this);
        pm.registerEvents(new Menu(), this);

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Parrot) {
                    Parrot parrot = (Parrot) entity;
                    if (parrot.getScoreboardTags().contains("Balloons")) {
                        parrot.remove();
                    }
                }
                if (entity instanceof ArmorStand) {
                    ArmorStand armorStand = (ArmorStand) entity;
                    if (armorStand.getScoreboardTags().contains("Balloons")) {
                        armorStand.remove();
                    }
                }

            }
        }

        this.getCommand("balloons").setExecutor(new Balloons());
        getCommand("balloons").setTabCompleter(new BalloonsTabCompleter());

        new BukkitRunnable() {
            public void run() {
                for (Player player : SummonBalloons.balloons.keySet()) {
                    Parrot parrot = SummonBalloons.balloons.get(player);

                    if (parrot.getLocation().distance(player.getLocation()) < 6D) {
                        if ((parrot).isLeashed()) {
                            Distance.line(parrot, (parrot).getLeashHolder());
                            if (Storage.showParticlesOnMove && !Storage.disabledBalloonParticles.contains(player.getUniqueId()))
                                parrot.getWorld().spawnParticle(Particle.DRIP_LAVA,parrot.getLocation(),20);
                        }

                    } else {
                        SummonBalloons.removeBalloon(player);

                        if (Main.getInstance().getConfig().getString("Balloons." + SummonBalloons.playerBalloons.get(player) + ".item") != null) {
                            ItemStack itemStack = new ItemStack(Material.valueOf(Main.getInstance().getConfig().getString("Balloons." + SummonBalloons.playerBalloons.get(player) + ".item")));
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            itemMeta.setCustomModelData(Main.getInstance().getConfig().getInt("Balloons." + SummonBalloons.playerBalloons.get(player) + ".custommodeldata"));
                            itemStack.setItemMeta(itemMeta);
                            SummonBalloons.summonBalloon(player, itemStack);

                        } else {
                            SummonBalloons.summonBalloon(player, GetSkull.createSkull(Main.getInstance().getConfig().getString("Balloons." + SummonBalloons.playerBalloons.get(player) + ".head")));

                        }
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 3L);


        FileConfiguration config = getConfig();

        config.addDefault("ShowOnlyBalloonsWithPermission", false);
        config.addDefault("ShowParticlesBalloonsOnRemove", true);
        config.addDefault("ShowParticlesOnMove", true);
        config.addDefault("NoBalloonsFound", "§bNo balloons found with this name.");
        config.addDefault("Economy", "vault");
        config.addDefault("NoBalloonsPermission", "§bYou do not have permission to use this balloons.");
        config.addDefault("BalloonsRemoved", "§bBalloons removed.");
        config.addDefault("BalloonsRemovedSound", "ENTITY_ENDERMAN_HURT");
        config.addDefault("BalloonsSummoned", "§bBalloons summoned.");
        config.addDefault("BalloonsSummonedSound", "ENTITY_ARROW_HIT_PLAYER");
        config.addDefault("BalloonsParticleToggleSound", "UI_BUTTON_CLICK");
        config.addDefault("BalloonsMenuName", "Balloons");
        config.addDefault("BalloonsMenuPrevious", "§7« §ePrevious");
        config.addDefault("BalloonsMenuNext", "§7« §eNext");
        config.addDefault("BalloonsMenuRemove", "§cRemove");
        config.addDefault("BalloonsMenuClickToSummon", "§6» §eClick to summon");
        config.addDefault("BalloonsMenuNoPermissionToSummon", "§cNo permission to summon");
        config.addDefault("CantOpenInventory", "§bYou can't open the inventory inside a vehicle.");

        if (config.get("Balloons") == null) {
            config.set("Balloons.shark_zekrom.permission", "Ballons.shark_zekrom");
            config.set("Balloons.shark_zekrom.displayname", "§eshark_zekrom");
            config.set("Balloons.shark_zekrom.price", 1000);
            config.set("Balloons.shark_zekrom.head", "ewogICJ0aW1lc3RhbXAiIDogMTYyNzA1NDA1Mjg5MCwKICAicHJvZmlsZUlkIiA6ICIzMzNhMjQ3ODk3MTE0MDA2YTE3ZDFmOTU4ZjhkMDZmMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJzaGFya196ZWtyb20iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDBjNzAyODQyZTc0MDM4ODA0YzYzNDUwZTU4YzI4ZTgwOGJjNmFiY2I1M2EwZjI0NTRjN2FkMmRkMDUwNmFhMyIKICAgIH0KICB9Cn0=");

            config.set("Balloons.item.permission", "Ballons.item");
            config.set("Balloons.item.item", "DIAMOND_HOE");
            config.set("Balloons.item.custommodeldata", 1);
            config.set("Balloons.item.price", 1000);
            config.set("Balloons.item.displayname", "§eitem");

        }

        try {
            if (!config.getBoolean("ShowParticlesOnMove")) Storage.showParticlesOnMove = false;
            if (config.getString("BalloonsRemovedSound") != null) Storage.removeSound = Sound.valueOf(config.getString("BalloonsRemovedSound"));
            if (config.getString("BalloonsSummonedSound") != null) Storage.summonSound = Sound.valueOf(config.getString("BalloonsSummonedSound"));
            if (config.getString("BalloonsParticleToggleSound") != null) Storage.particleToggleSound = Sound.valueOf(config.getString("BalloonsParticleToggleSound"));
        }catch (Exception ignored){}

        config.options().copyDefaults(true);
        saveConfig();

        Storage.showOnlyBalloonsWithPermission = config.getBoolean("ShowOnlyBalloonsWithPermission");

        ConfigurationSection cs = config.getConfigurationSection("Balloons");
        Menu.list.addAll(cs.getKeys(false));
        for (String ball : cs.getKeys(false)){
            Balloon balloon = new Balloon();
            balloon.setName(ball);
            balloon.setPrice(getConfig().getInt("Balloons." + ball + ".price"));
            balloon.setPermission(getConfig().getString("Balloons." + ball + ".permission"));
            balloon.setDisplayName(getConfig().getString("Balloons." + ball +".displayname"));
            if (getConfig().contains("Balloons." + ball + ".item")){
                balloon.setItem(true);
                balloon.setCustomModelData(getConfig().getInt( "Balloons." + ball + ".custommodeldata"));
                try{
                    balloon.setItemType(Material.valueOf(getConfig().getString("Balloons." + ball + ".item")));
                }catch (Exception error){
                    balloon.setItemType(Material.RED_STAINED_GLASS_PANE);
                }
            }
            else {
                balloon.setItem(false);
                balloon.setHeadTexture(getConfig().getString("Balloons." + ball + ".head"));
            }
            Menu.balloons.add(balloon);
        }

        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
            VaultEconomy economy = new VaultEconomy();
            Storage.economyType = Economy.EconomyType.VAULT;
            Storage.setEconomy(economy);
            economy.setupEconomy();
            VaultPermission permission = new VaultPermission();
            permission.setupPermissions();
            Storage.permissionServices = permission;
            Storage.hasPermissionServices = true;
        }


        Bukkit.getLogger().info(ChatColor.AQUA + "-----------======-----------");
        Bukkit.getLogger().info(ChatColor.DARK_AQUA + "Balloons enabled! v1.7");
        Bukkit.getLogger().info(ChatColor.GREEN + "Forked by EhsanMNA");
        Bukkit.getLogger().info(ChatColor.AQUA + "-----------======-----------");

    }

    @Override
    public void onDisable() {
        SummonBalloons.removeAllBalloon();
        Bukkit.getLogger().info(ChatColor.AQUA + "-----------======-----------");
        Bukkit.getLogger().info(ChatColor.RED + "  Balloons disabled!");
        Bukkit.getLogger().info(ChatColor.GREEN + "  Forked by EhsanMNA");
        Bukkit.getLogger().info(ChatColor.AQUA + "-----------======-----------");

    }
}
