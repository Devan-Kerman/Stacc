package net.devtech.stacc.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment (EnvType.CLIENT)
@Mixin (ItemRenderer.class)
public class ItemRendererMixin {
	@Unique private static final char[] POWER = {'K', 'M', 'B', 'T'};

	@Redirect (method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At (value = "INVOKE", target = "Ljava/lang/String;valueOf(I)Ljava/lang/String;"))
	private String render(int i) {
		int index = 0;
		while (i / 1000 != 0) {
			i /= 1000;
			index++;
		}

		if (index > 0) {
			return i + String.valueOf(POWER[index - 1]);
		} else {
			return String.valueOf(i);
		}
	}

	@Unique
	private ThreadLocal<MatrixStack> matrix = new ThreadLocal<>();
	@Inject (method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
	         at = @At (value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V"),
	         locals = LocalCapture.CAPTURE_FAILHARD)
	private void rescaleText(TextRenderer fontRenderer, ItemStack stack, int x, int y, String amountText, CallbackInfo ci, MatrixStack matrixStack, String string) {
		if (string.length() > 3) {
			matrixStack.push();
			matrixStack.scale(.5f, .5f, .5f);
			this.matrix.set(matrixStack);
		} else if (string.length() == 3) {
			matrixStack.push();
			matrixStack.scale(.75f, .75f, .75f);
			this.matrix.set(matrixStack);
		}

	}

	@Redirect (method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
	         at = @At (value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;draw()V"))
	private void push(VertexConsumerProvider.Immediate immediate) {
		immediate.draw();
		MatrixStack stacc = this.matrix.get();
		this.matrix.set(null);
		if(stacc != null) {
			stacc.pop();
		}
	}
}