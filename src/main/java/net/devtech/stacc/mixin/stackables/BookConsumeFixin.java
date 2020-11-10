package net.devtech.stacc.mixin.stackables;

import net.devtech.stacc.StaccGlobals;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayNetworkHandler;

@Mixin (ServerPlayNetworkHandler.class)
public class BookConsumeFixin {
	@Redirect (method = "method_31276",
			at = @At (value = "INVOKE",
					target = "Lnet/minecraft/entity/player/PlayerInventory;setStack(ILnet/minecraft/item/ItemStack;)" +
					         "V"))
	private void setStack(PlayerInventory inventory, int slot, ItemStack stack) {
		if (StaccGlobals.STACKABLE.get()) {
			stack.setCount(inventory.getStack(slot).getCount());
			inventory.setStack(slot, stack);
		}
	}
}
