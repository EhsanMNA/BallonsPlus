package be.shark_zekrom.utils;

import org.bukkit.Material;

public class Balloon {

    String name;
    String displayName;
    String permission;
    boolean item;
    Material itemType;
    int customModelData;
    int price = 0;
    String headTexture;

    public String getDisplayName() {
        return displayName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public boolean isItem() {
        return item;
    }

    public void setItem(boolean item) {
        this.item = item;
    }

    public Material getItemType() {
        return itemType;
    }

    public void setItemType(Material itemType) {
        this.itemType = itemType;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public void setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
    }

    public String getHeadTexture() {
        return headTexture;
    }

    public void setHeadTexture(String headTexture) {
        this.headTexture = headTexture;
    }



}
