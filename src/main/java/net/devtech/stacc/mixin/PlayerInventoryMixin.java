package net.devtech.stacc.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
	@Redirect(method = "addStack(ILnet/minecraft/item/ItemStack;)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxCount()I"))
	public int addStack(ItemStack stack, int slot, ItemStack realStack) {
		if(stack.isEmpty()) {
			return realStack.getMaxCount();
		} else {
			return stack.getMaxCount();
		}
	}
}
