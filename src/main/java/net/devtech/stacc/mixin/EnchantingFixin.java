package net.devtech.stacc.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.item.Item;

/**
 * fixes bug where players can't enchant stuff
 */
@Mixin (Item.class)
public abstract class EnchantingFixin {
	@ModifyConstant (method = "isEnchantable", constant = @Constant (intValue = 1, ordinal = 0))
	private int yeet(int one) {
		return this.getMaxCount();
	}

	@Shadow
	public abstract int getMaxCount();
}
