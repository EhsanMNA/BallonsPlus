package be.shark_zekrom.utils.economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultEconomy implements Economy{

    private static net.milkbowl.vault.economy.Economy econ = null;

    @Override
    public boolean hasMoney(Player player,int amount) {
        return econ.getBalance(player) >= amount;
    }

    @Override
    public int getMoney(Player player) {
        return (int) econ.getBalance(player);
    }

    @Override
    public void setMoney(Player player, int money) {
        econ.withdrawPlayer(player,econ.getBalance(player));
        econ.depositPlayer(player,money);
    }

    @Override
    public void addMoney(Player player, int amount) {
        econ.depositPlayer(player,amount);
    }

    @Override
    public void removeMoney(Player player, int amount) {
        econ.withdrawPlayer(player,amount);
    }

    public boolean setupEconomy() {
        RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }


}
