package net.devtech.stacc.mixin.stackables;

import net.devtech.stacc.StaccGlobals;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;

@Mixin (AnvilScreenHandler.class)
public abstract class AnvilDesktopHandSplayedFixin extends ForgingScreenHandler {
	@Shadow private int repairItemUsage;
	@Shadow @Final private Property levelCost;

	public AnvilDesktopHandSplayedFixin(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(type, syncId, playerInventory, context);
	}

	@Override
	@Shadow
	public abstract void updateResult();

	@Inject (method = "updateResult", at = @At (value = "INVOKE", target = "Lnet/minecraft/screen/AnvilScreenHandler;sendContentUpdates()V"))
	private void uncrob(CallbackInfo ci) {
		if (StaccGlobals.STACKABLE.get()) {
			ItemStack input = this.input.getStack(0);
			ItemStack input2 = this.input.getStack(1);
			int count = input.getCount();
			if (this.repairItemUsage == 0) {
				this.repairItemUsage = 1;
			}

			long amount = this.repairItemUsage;
			amount *= count;
			long xpAmount = this.levelCost.get();
			xpAmount *= count;
			if (amount > input2.getMaxCount() || xpAmount > Integer.MAX_VALUE) {
				amount = input2.getMaxCount();
				xpAmount = Integer.MAX_VALUE;
			}

			this.repairItemUsage = (int) amount;
			this.levelCost.set((int) xpAmount);

			if (this.repairItemUsage > input2.getCount()) {
				this.output.setStack(0, ItemStack.EMPTY);
				this.repairItemUsage = 0;
				this.levelCost.set(0);
			}
		}
	}
}
