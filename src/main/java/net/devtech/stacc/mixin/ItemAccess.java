package net.devtech.stacc.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.item.Item;

@Mixin (Item.class)
public interface ItemAccess {
	@Accessor
	void setMaxCount(int count);
}