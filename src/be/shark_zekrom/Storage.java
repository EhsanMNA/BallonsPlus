package be.shark_zekrom;

import be.shark_zekrom.utils.economy.Economy;
import be.shark_zekrom.utils.permission.PermissionServices;
import org.bukkit.Sound;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Storage {

    public static Economy.EconomyType economyType;

    public static boolean hasPermissionServices = false;

    public static PermissionServices permissionServices;

    private static Economy economy;

    public static boolean showParticlesOnMove = true;

    public static boolean showOnlyBalloonsWithPermission = false;

    public static Set<UUID> disabledBalloonParticles = new HashSet<>();

    public static Sound summonSound = Sound.ENTITY_ARROW_HIT_PLAYER;
    public static Sound removeSound = Sound.ENTITY_ENDERMAN_HURT;
    public static Sound particleToggleSound = Sound.UI_BUTTON_CLICK;






    public static Economy getEconomy(){
        return economy;
    }

    public static void setEconomy(Economy econ){
        economy = econ;
    }


}
