package com.duramec.time;

public final class TickGen {
	private long lastTick = Long.MIN_VALUE;

	/**
	 * Performs fast, imprecise generation of 100-ns ticks relative to the UUID
	 * epoch.
	 * 
	 * Note that the precision is only to the millisecond: further ticks are
	 * added synchronously after each millisecond. The only use for ticks after
	 * the millisecond marker is for identifying synchronicity, not elapsed
	 * time.
	 * 
	 * This means in practicality that there will be a much higher tick density
	 * after the beginning of the millisecond barrier than prior to a new one.
	 * 
	 * @return
	 */
	public synchronized final long next() {
		long ms = System.currentTimeMillis();
		long tick = (ms * 10000L) + 0x01B21DD213814000L;

		if (tick > lastTick) {
			lastTick = tick;
		} else {
			tick = ++lastTick;
		}
		return tick;
	}

}
