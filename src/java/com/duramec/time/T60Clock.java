package com.duramec.time;

import java.lang.Thread;

public final class T60Clock {

	private static final long startTick;

	private static final long startNanos;

	static {
		startNanos = System.nanoTime();
		startTick = (System.currentTimeMillis() * 10000L) + 0x01B21DD213814000L;
		try {
			// wait a bit in order to ensure no negative numbers
			// will be generated from offset of a potential shift of
			// millisecond down because of clock skew adjustment.
			Thread.sleep(3);
		} catch (InterruptedException e) {
			// do nothing
		}
	}

	public synchronized final long tick() {
		return startTick + ((System.nanoTime() - startNanos) / 100L);
	}

}