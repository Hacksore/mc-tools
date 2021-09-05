package com.hacksore.mctools.mixin;


import com.hacksore.mctools.MCToolsMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

	@Inject(method = "attackBlock", at = @At(value = "HEAD"))
	private void hookBlockAttack(final BlockPos pos, final Direction direction, final CallbackInfoReturnable<Boolean> cir) {
		MCToolsMod.quickSwitch(pos);
	}

}
