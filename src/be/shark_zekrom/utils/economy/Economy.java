package be.shark_zekrom.utils.economy;

import org.bukkit.entity.Player;

public interface Economy {

    public boolean hasMoney(Player player,int amount);

    public int getMoney(Player player);

    public void setMoney(Player player,int money);

    public void addMoney(Player player,int amount);

    public void removeMoney(Player player,int amount);

    public enum EconomyType {
        VAULT
    }

}
