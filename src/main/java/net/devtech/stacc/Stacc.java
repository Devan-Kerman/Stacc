package net.devtech.stacc;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.devtech.stacc.mixin.StaccMixin;

import net.minecraft.item.Item;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

public interface Stacc {
	int DEFAULT = 1_000_000_000;

	static void onInitialize() {
		for (Item item : Registry.ITEM) {
			System.out.println("test");
			setMax(item, DEFAULT);
		}
		System.out.println("Stacc has initialized");
		Identifier val = new Identifier("stacc", "stacc");
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new IdentifiableResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return val;
			}

			@Override
			public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
				return CompletableFuture.supplyAsync(() -> {
					System.out.println("Stacc loading properties...");
					Object2IntMap<Item> ov = new Object2IntOpenHashMap<>();
					ov.defaultReturnValue(DEFAULT);
					try {
						for (Resource stacc : manager.getAllResources(new Identifier("stacc", "stacc.properties"))) {
							Properties properties = new Properties();
							properties.load(stacc.getInputStream());
							properties.forEach((i, c) -> {
								String val = (String) i;
								int count = Integer.parseInt((String) c);
								if (val.equals("default")) {
									ov.defaultReturnValue(count);
								}
								ov.put(Registry.ITEM.get(new Identifier(val)), count);
							});
						}
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
					return ov;
				}, prepareExecutor).thenCompose(synchronizer::whenPrepared).thenAcceptAsync(o -> {
					for (Item item : Registry.ITEM) {
						setMax(item, o.getInt(item));
					}
				});
			}
		});
	}

	static void setMax(Item item, int max) {
		((StaccMixin.ItemAccess) item).setMaxCount(max);
	}
}
