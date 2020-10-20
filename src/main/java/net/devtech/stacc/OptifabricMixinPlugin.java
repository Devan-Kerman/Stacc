package net.devtech.stacc;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.fabricmc.loader.api.FabricLoader;

public class OptifabricMixinPlugin implements IMixinConfigPlugin {
	private static final boolean OPTIFABRIC = FabricLoader.getInstance().isModLoaded("optifabric");

	@Override
	public void onLoad(String mixinPackage) {
		if (OPTIFABRIC) {
			Logger logger = Logger.getLogger("Stacc");
			logger.warning("=========== Optifabric Detected! ===========");
			logger.warning("Optifine is a closed source and invasive mod that uses an archaic method of patching");
			logger.warning(
					"This makes it difficult/impossible for mod authors to deal with it and fix issues when they " +
					"arise");
			logger.warning("So, for everyone's sake consider using Sodium and Optifine alternatives");
			logger.warning("If you're using Optifine for performance, use Sodium, it's better");
			logger.warning("If you're using Optifine for shaders, use Canvas");
			logger.warning(
					"If you're using Optifine for customization, there are many fabric mods that offer similar " +
					"features");
			logger.warning(
					"If you're using Optifine for resource packs, there's CBT, and other mods that do the same thing " +
					"as well, most with optifine format support");
			logger.warning("https://gist.github.com/LambdAurora/1f6a4a99af374ce500f250c6b42e8754");
			logger.warning("=========== Optifabric Detected! ===========");
		}
	}

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
