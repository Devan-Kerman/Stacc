package net.devtech.stacc;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.fabricmc.loader.api.FabricLoader;

public class OptifabricMixinPlugin implements IMixinConfigPlugin {
	public static final boolean OPTIFABRIC = FabricLoader.getInstance().isModLoaded("optifabric");

	@Override
	public void onLoad(String mixinPackage) {}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return !OPTIFABRIC || targetClassName.startsWith("net.devtech.stacc.mixin.optifabric") || !targetClassName
				                                                                                           .startsWith(
						                                                                                           "net.devtech.stacc.mixin.disable_optifabric");
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

	@Override
	public List<String> getMixins() {return null;}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
