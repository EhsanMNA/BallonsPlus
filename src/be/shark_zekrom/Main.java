package be.shark_zekrom;

import be.shark_zekrom.commands.Balloons;
import be.shark_zekrom.commands.BalloonsTabCompleter;
import be.shark_zekrom.inventory.Menu;
import be.shark_zekrom.utils.Distance;
import be.shark_zekrom.utils.GetSkull;
import be.shark_zekrom.listener.Listener;
import be.shark_zekrom.utils.SummonBalloons;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Main extends JavaPlugin {

    public static boolean showParticlesOnMove = true;
    public Set<UUID> disabledBalloonParticles = new HashSet<>();
    public Sound summonSound = Sound.ENTITY_ARROW_HIT_PLAYER;
    public Sound removeSound = Sound.ENTITY_ENDERMAN_HURT;
    public Sound particleToggleSound = Sound.UI_BUTTON_CLICK;
    private static Main instance;
    public static Main getInstance() {
        return instance;
    }

    public static boolean showOnlyBallonsWithPermission = false;

    @Override
    public void onEnable() {
        instance = this;
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new Listener(), this);
        pm.registerEvents(new Menu(), this);

        this.getCommand("balloons").setExecutor(new Balloons());
        getCommand("balloons").setTabCompleter(new BalloonsTabCompleter());

        new BukkitRunnable() {
            public void run() {
                for (Player player : SummonBalloons.balloons.keySet()) {
                    Parrot parrot = SummonBalloons.balloons.get(player);

                    if (parrot.getLocation().distance(player.getLocation()) < 6D) {
                        if ((parrot).isLeashed()) {
                            Distance.line(parrot, (parrot).getLeashHolder());
                            if (Main.showParticlesOnMove && !disabledBalloonParticles.contains(player.getUniqueId()))
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
            config.set("Balloons.shark_zekrom.head", "ewogICJ0aW1lc3RhbXAiIDogMTYyNzA1NDA1Mjg5MCwKICAicHJvZmlsZUlkIiA6ICIzMzNhMjQ3ODk3MTE0MDA2YTE3ZDFmOTU4ZjhkMDZmMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJzaGFya196ZWtyb20iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDBjNzAyODQyZTc0MDM4ODA0YzYzNDUwZTU4YzI4ZTgwOGJjNmFiY2I1M2EwZjI0NTRjN2FkMmRkMDUwNmFhMyIKICAgIH0KICB9Cn0=");

            config.set("Balloons.item.permission", "Ballons.item");
            config.set("Balloons.item.item", "DIAMOND_HOE");
            config.set("Balloons.item.custommodeldata", 1);
            config.set("Balloons.item.displayname", "§eitem");

        }

        try {
            if (!config.getBoolean("ShowParticlesOnMove")) showParticlesOnMove = false;
            if (config.getString("BalloonsRemovedSound") != null) removeSound = Sound.valueOf(config.getString("BalloonsRemovedSound"));
            if (config.getString("BalloonsSummonedSound") != null) summonSound = Sound.valueOf(config.getString("BalloonsSummonedSound"));
            if (config.getString("BalloonsParticleToggleSound") != null) particleToggleSound = Sound.valueOf(config.getString("BalloonsParticleToggleSound"));
        }catch (Exception ignored){}

        config.options().copyDefaults(true);
        saveConfig();


        ConfigurationSection cs = config.getConfigurationSection("Balloons");
        Menu.list.addAll(cs.getKeys(false));

        showOnlyBallonsWithPermission = config.getBoolean("ShowOnlyBalloonsWithPermission");

        Bukkit.getLogger().info(ChatColor.AQUA + "-----------======-----------");
        Bukkit.getLogger().info(ChatColor.DARK_AQUA + "Balloons enabled !");
        Bukkit.getLogger().info(ChatColor.GREEN + "Forked by EhsanMNA");
        Bukkit.getLogger().info(ChatColor.AQUA + "-----------======-----------");

    }

    @Override
    public void onDisable() {
        SummonBalloons.removeAllBalloon();
        Bukkit.getLogger().info(ChatColor.AQUA + "-----------======-----------");
        Bukkit.getLogger().info(ChatColor.RED + "  Balloons disabled !");
        Bukkit.getLogger().info(ChatColor.GREEN + "  Forked by EhsanMNA");
        Bukkit.getLogger().info(ChatColor.AQUA + "-----------======-----------");

    }
}
