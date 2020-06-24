package net.devtech.stacc.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.server.network.ServerPlayNetworkHandler;

@Mixin (ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
	@ModifyConstant (method = "onCreativeInventoryAction", constant = @Constant (intValue = 64))
	private int max(int old) {
		return Integer.MAX_VALUE;
	}
}
