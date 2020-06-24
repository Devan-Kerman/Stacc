package net.devtech.stacc.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.inventory.Inventory;

@Mixin (Inventory.class)
public interface InventoryMixin {
	/**
	 * @author HalfOf2
	 * @reason increase max stack size
	 */
	@Overwrite
	default int getInvMaxStackAmount() {
		return Integer.MAX_VALUE;
	}
}