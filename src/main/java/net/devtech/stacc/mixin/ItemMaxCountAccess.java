package net.devtech.stacc.mixin;

import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin (Item.class)
public interface ItemMaxCountAccess {
	@Mutable
	@Accessor
	void setMaxCount(int maxCount);
}
