package net.devtech.stacc;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.logging.LogUtils;
import net.devtech.stacc.mixin.ItemMaxCountAccess;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class StaccLoader implements ModInitializer {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final String MOD_ID = "stacc";

	@Override
	public void onInitialize() {
		Map<Item, Integer> originalSizes = new IdentityHashMap<>();
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new IdentifiableResourceReloadListener() {
			final Identifier id = id("cfg");
			final Identifier resourceId = id("config.json");

			@Override
			public Identifier getFabricId() {
				return this.id;
			}

			@Override
			public CompletableFuture<Void> reload(
				Synchronizer synchronizer,
				ResourceManager manager,
				Profiler prepareProfiler,
				Profiler applyProfiler,
				Executor prepareExecutor,
				Executor applyExecutor
			) {
				return CompletableFuture.supplyAsync(() -> {
					Map<Identifier, Integer> stackSizes = new HashMap<>();
					List<Resource> resources = manager.getAllResources(this.resourceId);
					for (Resource resource : resources) {
						try(BufferedReader reader = resource.getReader()) {
							JsonObject keys = JsonHelper.deserialize(reader);
							for (var entry : keys.entrySet()) {
								Identifier itemId = new Identifier(entry.getKey());
								JsonElement value = entry.getValue();
								if(value.isJsonNull()) {
									stackSizes.remove(itemId);
								} else if(value.isJsonArray() || value.isJsonObject()) {
									throw new IOException("Invalid stack size for %s: %s! Must be: {[0-1,000,000,000], \"default\", or null}".formatted(itemId, value));
								} else if(value instanceof JsonPrimitive p) {
									if(p.isNumber()) {
										int size = p.getAsNumber().intValue();
										if(size < 0 || size > 1_000_000_000) {
											throw new IOException("Invalid stack size for %s: %s! Must be: {[0-1,000,000,000], \"default\", or null}".formatted(itemId, size));
										} else {
											stackSizes.put(itemId, size);
										}
									} else if(p.isString() && p.getAsString().equals("default")) {
										stackSizes.remove(itemId);
									} else {
										throw new IOException("Invalid stack size for %s: %s! Must be: {[0-1,000,000,000], \"default\", or null}".formatted(itemId, value));
									}
								}
							}
						} catch (IOException e) {
							throw rethrow(new IOException("Invalid stack:config.json in pack \"%s\", format is {\"item:id\": 43, \"item:id2\": 5785, ...}!".formatted(resource.getPack().getName()), e));
						}
					}
					return stackSizes;
				}, prepareExecutor).thenCompose(synchronizer::whenPrepared).thenAcceptAsync(u -> {
					Map<Item, Integer> restore = new HashMap<>(originalSizes);
					for (var entry : u.entrySet()) {
						Identifier key = entry.getKey();
						Integer value = entry.getValue();
						Item item = Registries.ITEM.get(key);
						originalSizes.putIfAbsent(item, item.getMaxCount());
						restore.remove(item);
						if(item == Items.AIR) {
							LOGGER.warn("No item found for: %s".formatted(key));
						} else {
							int old = item.getMaxCount();
							((ItemMaxCountAccess)item).setMaxCount(value);
							LOGGER.info("Changed max count of %s from %s to %s!".formatted(key, old, value));
						}
					}
					restore.forEach((item, originalCount) -> {
						((ItemMaxCountAccess) item).setMaxCount(originalCount);
						originalSizes.remove(item);
					});
					StaccGlobals.lastSize = -1;
				}, applyExecutor);
			}
		});
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}

	/**
	 * @return nothing, because it throws
	 * @throws T rethrows {@code throwable}
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Throwable> RuntimeException rethrow(Throwable throwable) throws T {
		throw (T) throwable;
	}
}
