package com.duramec.time;

import java.lang.Thread;

public final class Clock {

	private final long startTick;

	private final long startNanos;

	public Clock() {
		startNanos = System.nanoTime();
		startTick = ((startNanos / 1000000L) * 10000L) + 0x01B21DD213814000L;
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
		long now = System.nanoTime();
		return startTick + ((now - startNanos) / 100L);
	}

}