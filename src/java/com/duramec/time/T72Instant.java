package com.duramec.time;

public class T72Instant {

	final private T60Instant t60;

	final private long clockSeq;

	public T72Instant(T60Instant t60, long clockSeq) {
		this.t60 = t60;
		this.clockSeq = clockSeq;
	}

	/*
	 * public static T72Instant parse(String string) {
	 * 
	 * }
	 */

	@Override
	public String toString() {
		return "";
	}

	public T60Instant getT60() {
		return t60;
	}

	public long getClockSeq() {
		return clockSeq;
	}

}