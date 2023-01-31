package net.devtech.stacc;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class StaccGlobals {
	public static final ThreadLocal<Long> COUNT = ThreadLocal.withInitial(() -> 0L);
	static int max, lastSize;

	private StaccGlobals() {}

	// ideally this would use a callback, but I am lazy
	public static int getMax() {
		Registry<Item> items = Registries.ITEM;
		int size = items.getIds().size(), max;
		if (lastSize != size) {
			StaccGlobals.max = max = items.stream().mapToInt(Item::getMaxCount).max().orElse(0);
			lastSize = size;
		} else {
			max = StaccGlobals.max;
		}
		return max;
	}
}
