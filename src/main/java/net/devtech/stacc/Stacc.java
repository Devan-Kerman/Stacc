package net.devtech.stacc;

import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import net.devtech.stacc.mixin.ItemAccess;

import net.minecraft.item.Item;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;


public interface Stacc {
	ThreadLocal<Long> COUNT = ThreadLocal.withInitial(() -> 0L);
	int DEFAULT = 1_000_000_000;
	AtomicBoolean STACKABLE = new AtomicBoolean(false);

	static void onInitialize() {
		Identifier val = new Identifier("stacc", "stacc");
		ResourceManagerHelper.get(ResourceType.SERVER_DATA)
		                     .registerReloadListener(new IdentifiableResourceReloadListener() {
			                     @Override
			                     public Identifier getFabricId() {
				                     return val;
			                     }

			                     @Override
			                     public CompletableFuture<Void> reload(ResourceReloadListener.Synchronizer synchronizer,
					                     ResourceManager manager,
					                     Profiler prepareProfiler,
					                     Profiler applyProfiler,
					                     Executor prepareExecutor,
					                     Executor applyExecutor) {
				                     return CompletableFuture.supplyAsync(() -> {
					                     System.out.println("[Stacc] Loading Properties...");
					                     Properties properties = new Properties();
					                     try {
						                     for (Resource stacc : manager.getAllResources(new Identifier("stacc",
								                     "stacc.properties"))) {
							                     properties.load(stacc.getInputStream());
						                     }
					                     } catch (Throwable e) {
						                     System.out.println("[Stacc] Failed to load properties!");
						                     e.printStackTrace();
					                     }
					                     return properties;
				                     }, prepareExecutor).thenCompose(synchronizer::whenPrepared).thenAcceptAsync(p -> {
					                     boolean stackable = p.getProperty("enable_stackable", "false").equals("true");
					                     STACKABLE.set(stackable);
					                     if (stackable) {
						                     System.out.println(
								                     "[Stacc] Stackable Unstackables is an unsupported feature of " +
								                     "Stacc, use at your own risk!");
					                     }

					                     try {
						                     int _default = Optional.ofNullable(p.getProperty("default"))
						                                            .map(Integer::new).orElse(DEFAULT);
						                     for (Item item : Registry.ITEM) {
							                     if (stackable || item.getMaxCount() != 1) {
								                     setMax(item, _default);
							                     }
						                     }
					                     } catch (Throwable t) {
						                     System.out.println("[Stacc] default stack size disabled!");
						                     t.printStackTrace();
					                     }
					                     try {

						                     p.forEach((k, v) -> {
							                     Item item = Registry.ITEM.get(new Identifier(k.toString()));
							                     if (stackable || item.getMaxCount() != 1) {
								                     setMax(item, Integer.parseInt((String) v));
							                     }
						                     });
					                     } catch (Throwable t) {
						                     System.out.println("[Stacc] Error on init!");
						                     t.printStackTrace();
					                     }
				                     });
			                     }
		                     });
		System.out.println("[Stacc] initialized!");
	}

	static void setMax(Item item, int max) {
		((ItemAccess) item).setMaxCount(max);
	}
}
