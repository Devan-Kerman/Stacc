package net.devtech.stacc.mixin;

import net.devtech.stacc.ItemCountRenderHandler;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment (EnvType.CLIENT)
@Mixin (DrawContext.class)
public class RenderItemCountFixin {

    @Shadow @Final private MatrixStack matrices;

    @Redirect (method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			at = @At (value = "INVOKE", target = "Ljava/lang/String;valueOf(I)Ljava/lang/String;"))
	private String render(int i) {
		return ItemCountRenderHandler.getInstance().toConsiseString(i);
	}

	@Redirect (method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			at = @At (value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Ljava/lang/String;)I"))
	private int width(TextRenderer renderer, String text) {
		return (int) (renderer.getWidth(text) * ItemCountRenderHandler.getInstance().scale(text));
	}

	@Inject (method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			at = @At (value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", shift = At.Shift.AFTER),
			locals = LocalCapture.CAPTURE_FAILHARD)
	private void rescaleText(TextRenderer textRenderer, ItemStack stack, int x, int y, String countOverride, CallbackInfo ci, String string) {
		float f = ItemCountRenderHandler.getInstance().scale(string);
		if (f != 1f) {
			this.matrices.translate(x * (1 - f), y * (1 - f) + (1 - f) * 16, 0);
			this.matrices.scale(f, f, f);
		}
	}
}