package net.devtech.stacc.mixin;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import io.netty.buffer.ByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.PacketByteBuf;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Mixin (PacketByteBuf.class)
public abstract class StaccMixin {
	@Inject (method = "writeItemStack",
	         at = @At (value = "INVOKE",
	                   target = "Lnet/minecraft/util/PacketByteBuf;writeCompoundTag(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/util/PacketByteBuf;"))
	private void write(ItemStack itemStack, CallbackInfoReturnable<PacketByteBuf> cir) {
		this.writeInt(itemStack.getCount());
	}

	@Shadow
	public abstract ByteBuf writeInt(int i);

	@ModifyArg (method = "readItemStack",
	            at = @At (value = "INVOKE",
	                      target = "Lnet/minecraft/item/ItemStack;<init>(Lnet/minecraft/item/ItemConvertible;I)V"),
	            index = 1)
	private int doThing(int amount) {
		return this.readInt();
	}

	@Shadow
	public abstract int readInt();

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


	@Mixin (Item.class)
	public interface ItemAccess {
		@Accessor
		void setMaxCount(int count);
	}

	@Mixin (ItemStack.class)
	private static abstract class ItemStackMixin {
		@Environment (EnvType.CLIENT) private static final NumberFormat FORMAT = NumberFormat.getNumberInstance(Locale.US);
		@Shadow private int count;

		@Inject (at = @At ("TAIL"),
		         method = "<init>(Lnet/minecraft/nbt/CompoundTag;)V")
		void onDeserialization(CompoundTag tag, CallbackInfo callbackInformation) {
			if (tag.contains("countInteger")) {
				this.count = tag.getInt("countInteger");
			}
		}

		@Inject (at = @At ("TAIL"),
		         method = "toTag(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/nbt/CompoundTag;")
		void onSerialization(CompoundTag tag, CallbackInfoReturnable<CompoundTag> callbackInformationReturnable) {
			if (this.count > Byte.MAX_VALUE) {
				tag.putInt("countInteger", this.count);
				// fix overflow bug
				tag.putByte("Count", (byte) 64);
			}
		}

		@Environment (EnvType.CLIENT)
		@Inject (method = "getTooltip",
		         at = @At ("RETURN"),
		         cancellable = true)
		private void addOverflowTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
			if (this.getCount() >= 1000) {
				List<Text> texts = cir.getReturnValue();
				texts.add(1, new LiteralText(FORMAT.format(this.getCount())).formatted(Formatting.GRAY));
			}
		}

		@Shadow
		public abstract int getCount();
	}

	@Mixin (ItemRenderer.class)
	private static class ItemRendererMixin {
		@Unique private static final char[] POWER = {'K', 'M', 'B', 'T'};

		@Redirect (method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
		           at = @At (value = "INVOKE",
		                     target = "Ljava/lang/String;valueOf(I)Ljava/lang/String;"))
		private String render(int i) {
			int index = 0;
			while (i / 1000 != 0) {
				i /= 1000;
				index++;
			}

			if (index > 0) { return i + String.valueOf(POWER[index - 1]); } else return String.valueOf(i);
		}
	}
}
