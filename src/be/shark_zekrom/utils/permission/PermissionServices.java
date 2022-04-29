package be.shark_zekrom.utils.permission;

import org.bukkit.entity.Player;

public interface PermissionServices {

    public void addPermission(Player player,String permission);

    public boolean hasPermission(Player player,String permission);

    public void removePermission(Player player,String permission);

}
