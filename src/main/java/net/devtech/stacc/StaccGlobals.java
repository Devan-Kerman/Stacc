package net.devtech.stacc;

import java.util.concurrent.atomic.AtomicBoolean;

public interface StaccGlobals {
	ThreadLocal<Long> COUNT = ThreadLocal.withInitial(() -> 0L);
	AtomicBoolean STACKABLE = new AtomicBoolean(false);
}
