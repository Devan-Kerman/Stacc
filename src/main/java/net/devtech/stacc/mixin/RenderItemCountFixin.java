package net.devtech.stacc.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment (EnvType.CLIENT)
@Mixin (ItemRenderer.class)
public class RenderItemCountFixin {
	@Unique private static final char[] POWER = {
			'K',
			'M',
			'B',
			'T'
	};

	@Redirect (method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			at = @At (value = "INVOKE", target = "Ljava/lang/String;valueOf(I)Ljava/lang/String;"))
	private String render(int i) {
		int index = 0;
		if (i > 9999) {
			while (i / 1000 != 0) {
				i /= 1000;
				index++;
			}
		}

		if (index > 0) {
			return i + String.valueOf(POWER[index - 1]);
		} else {
			return String.valueOf(i);
		}
	}

	@Redirect (method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			at = @At (value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Ljava/lang/String;)I"))
	private int width(TextRenderer renderer, String text) {
		return (int) (renderer.getWidth(text) * stacc_getScale(text));
	}

	@Unique
	private static float stacc_getScale(String string) {
		if (string.length() > 3) {
			return .5f;
		} else if (string.length() == 3) {
			return .75f;
		}
		return 1f;
	}

	@Inject (method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			at = @At (value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V", shift = At.Shift.AFTER),
			locals = LocalCapture.CAPTURE_FAILHARD)
	private void rescaleText(TextRenderer fontRenderer, ItemStack stack, int x, int y, String amountText, CallbackInfo ci, MatrixStack matrixStack,
	                         String string) {
		float f = stacc_getScale(string);
		if (f != 1f) {
			matrixStack.translate(x * (1 - f), y * (1 - f) + (1 - f) * 16, 0);
			matrixStack.scale(f, f, f);
		}
	}
}