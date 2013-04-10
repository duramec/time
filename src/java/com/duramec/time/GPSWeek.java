package com.duramec.time;

public class GPSWeek implements Comparable<GPSWeek> {

	private final int absoluteWeek;

	private final T60Instant begin;

	private final T60Instant end;

	public GPSWeek(int week) {
		assert (week >= 0);
		this.absoluteWeek = week;
		long elapsedSecondsBegin = week * 86400 * 7;
		long elapsedSecondsEnd = (week + 1) * 86400 * 7;
		GPSInstant gpsBegin = new GPSInstant(elapsedSecondsBegin, 0L);
		GPSInstant gpsEnd = new GPSInstant(elapsedSecondsEnd, 0L);
		this.begin = gpsBegin.toT60Instant();
		this.end = gpsEnd.toT60Instant();
	}

	public int getAbsoluteWeek() {
		return absoluteWeek;
	}

	public T60Instant getBegin() {
		return begin;
	}

	public T60Instant getEnd() {
		return end;
	}

	public boolean contains(T60Instant instant) {
		return (instant.compareTo(begin) >= 0 && instant.compareTo(end) < 0);
	}

	@Override
	public int compareTo(GPSWeek o) {
		if (this.absoluteWeek < o.absoluteWeek) return -1;
		if (this.absoluteWeek > o.absoluteWeek) return 1;
		return 0;
	}

}