package com.hacksore.mctools;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;
import net.minecraft.item.Items;

public class MCToolsMod implements ModInitializer {
	public static boolean autoToolState = true;
	public static int ARMOR_CHESTPLATE_INDEX = 38;

	@Override
	public void onInitialize() {
		System.out.println("[MCToolsMod]");
		KeyBinding autotoolBind = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.mctools.autotool", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_HOME, "key.generic.autotool"));
		KeyBinding elytraSwapBind = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.mctools.elytraSwap", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_COMMA, "key.generic.elytraSwap"));

		// toggle
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (autotoolBind.wasPressed()) {
				String status = MCToolsMod.autoToolState ? "disabled" : "enabled";
				client.player.sendMessage(new LiteralText("[Autotool] " + status), false);

				MCToolsMod.autoToolState = !MCToolsMod.autoToolState;
			}

			while (elytraSwapBind.wasPressed()) {
				client.player.sendMessage(new LiteralText("[ElytraSwap]"), false);
				MCToolsMod.elytraSwap();
			}
		});
	}

	public static void quickSwitch(BlockPos pos) {
		if (!MCToolsMod.autoToolState) {
			return;
		}

		MinecraftClient mc = MinecraftClient.getInstance();
		BlockState bs = mc.world.getBlockState(pos);
		int currentSlot = mc.player.getInventory().selectedSlot;

		int bestSlot = 0;
		float maxItemMultiplier = 0;
		for (int i = 0; i <= 8; i++) {
			ItemStack item = mc.player.getInventory().getStack(i);
			float itemSpeed = item.getMiningSpeedMultiplier(bs);
			if (itemSpeed > maxItemMultiplier) {
				maxItemMultiplier = itemSpeed;
				bestSlot = i;
			}
		}

		// switch
		if (bestSlot != currentSlot) {
			mc.player.getInventory().selectedSlot = bestSlot;
		}

	}

	public static void elytraSwap() {
		MinecraftClient mc = MinecraftClient.getInstance();
		int foundElytraSlot = Util.findItemIndex(Items.ELYTRA);
		int foundChestplateSlot = Util.findItemIndex(Items.DIAMOND_CHESTPLATE);

		// prolly not even needed lmao
		mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.OPEN_INVENTORY));

		int syncId = mc.player.playerScreenHandler.syncId;
		ItemStack currentArmorItem = mc.player.getInventory().getStack(MCToolsMod.ARMOR_CHESTPLATE_INDEX);

		int itemToSwitchIndex = currentArmorItem.getItem() == Items.ELYTRA ? foundChestplateSlot : foundElytraSlot;
		int slot = Util.convertClientSlotToServerSlot(MCToolsMod.ARMOR_CHESTPLATE_INDEX);
		mc.interactionManager.clickSlot(0, slot, itemToSwitchIndex, SlotActionType.SWAP, mc.player);

		// "anti-cheat" hehe xd
		mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(syncId));

	}

}
