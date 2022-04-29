package be.shark_zekrom.utils.permission;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultPermission implements PermissionServices{

    private static Permission perms = null;

    @Override
    public void addPermission(Player player, String permission) {
        perms.playerAdd(player,permission);
    }

    @Override
    public boolean hasPermission(Player player, String permission) {
        return perms.has(player,permission);
    }

    @Override
    public void removePermission(Player player, String permission) {
        perms.playerRemove(player,permission);
    }

    public boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
}
