package net.devtech.stacc.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

@Mixin (ServerPlayNetworkHandler.class)
public class BookConsumeFixin {
	@Shadow public ServerPlayerEntity player;

	@Redirect (method = "onBookUpdate",
			at = @At (value = "INVOKE",
					target = "Lnet/minecraft/server/network/ServerPlayerEntity;setStackInHand(Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;)" +
					         "V"))
	private void setStack(ServerPlayerEntity entity, Hand hand, ItemStack stack, BookUpdateC2SPacket packet) {
		stack.setCount(this.player.getStackInHand(packet.getHand())
		                          .getCount());
		entity.setStackInHand(hand, stack);
	}
}
