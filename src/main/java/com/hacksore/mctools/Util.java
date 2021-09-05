package com.hacksore.mctools;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Util {
    public static int findItemIndex(Item item) {
        MinecraftClient mc = MinecraftClient.getInstance();

        for (int i = 0; i <= 40; i++) {
            ItemStack is = mc.player.getInventory().getStack(i);

            if (is.getItem() == item){
                return i;
            }
        }

        return -1;
    }

    public static int convertClientSlotToServerSlot(int slot) {
        if (slot < 8) return 36 + slot;
        if (slot > 8 && slot < 36 ) return slot;
        if (slot > 35 && slot < 40) return 39 - slot + 5;

        return -1;
    }
}
