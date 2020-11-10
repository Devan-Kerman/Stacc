package net.devtech.stacc.mixin.stackables;

import net.devtech.stacc.StaccGlobals;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.item.Item;

/**
 * fixes bug where players can't enchant stuff
 */
@Mixin (Item.class)
public abstract class EnchantingFixin {
	@Redirect (method = "isEnchantable",
			at = @At (value = "INVOKE", target = "Lnet/minecraft/item/Item;getMaxCount()I"))
	private int yeet(Item item) {
		if (StaccGlobals.STACKABLE.get()) {
			return 1;
		} else {
			return item.getMaxCount();
		}
	}
}
