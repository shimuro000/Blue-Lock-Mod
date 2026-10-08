package com.bluelockmod.player;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public final class ProfileMenu extends AbstractContainerMenu {
    public ProfileMenu(int id, Inventory inventory) { super(MenuType.GENERIC_9x1, id); }
    @Override public boolean stillValid(Player player) { return true; }
}
