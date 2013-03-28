package com.duramec.time;

import java.io.Serializable;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class GPSInstant implements Comparable<GPSInstant>, Serializable {

	private static final long serialVersionUID = 1610724597023251748L;

	private static final long GPS_TO_LILIAN = Epoch.GPS.secondsPosixEpoch()
			- Epoch.LILIAN.secondsPosixEpoch();

	/**
	 * Regular expression pattern describing .toString and parsing format of
	 * this class. Format is:
	 * 
	 * (seconds-from-epoch).(fractional)GPS
	 * 
	 * where parentheses are not included.
	 */
	private static Pattern pattern = Pattern
			.compile("(\\d{1,12})\\.(\\d{3,15})TAI");

	/**
	 * Number of SI seconds in a GPS week.
	 */
	private static final long secondsInGpsWeek = 7 * 86400;

	/**
	 * Number of weeks in a GPS rollover cycle determined by the 10-bit
	 * constraint on satellites.
	 */
	private static final int weeksInGpsCycle = 1024;

	/**
	 * Number of SI seconds in a 10-bit GPS cycle.
	 */
	private static final long secondsInGpsCycle = weeksInGpsCycle
			* secondsInGpsWeek;

	/**
	 * 10-bit cycle number.
	 */
	private final int cycle;

	/**
	 * Week number in current 10-bit GPS cycle.
	 */
	private final int gpsWeek;

	/**
	 * Absolute number of weeks elapsed since beginning of GPS epoch.
	 */
	private final int absoluteWeek;

	/**
	 * Seconds elapsed in the current GPS week.
	 */
	private final int secondsOfGpsWeek;

	/**
	 * Number of SI seconds elapsed since beginning of GPS epoch.
	 */
	private final long seconds;

	/**
	 * Fractional seconds of current second.
	 */
	private final long nanos;

	/**
	 * Construct GPSInstant in the way GPS natively transmits packets. Requires
	 * a cycle number to remove ambiguity from the GPS cycle rollover.
	 * 
	 * @param cycle
	 * @param gpsWeek
	 * @param secondsOfWeek
	 * @param nanos
	 */
	public GPSInstant(int cycle, int gpsWeek, int secondsOfWeek, long nanos) {
		this.cycle = cycle;
		this.gpsWeek = gpsWeek;
		this.secondsOfGpsWeek = secondsOfWeek;
		this.absoluteWeek = (cycle * weeksInGpsCycle) + gpsWeek;
		this.seconds = (cycle * secondsInGpsCycle)
				+ (gpsWeek * secondsInGpsWeek) + secondsOfWeek;
		this.nanos = nanos;
	}

	/**
	 * Construct GPSInstant from a count of seconds since the epoch and a
	 * fractional component.
	 * 
	 * @param seconds
	 * @param nanos
	 */
	public GPSInstant(long seconds, long nanos) {
		this.seconds = seconds;
		this.nanos = nanos;
		this.cycle = (int) (seconds / secondsInGpsCycle);
		this.absoluteWeek = (int) (seconds / secondsInGpsWeek);
		this.gpsWeek = absoluteWeek - (cycle * weeksInGpsCycle);
		this.secondsOfGpsWeek = (int) (seconds - (cycle * secondsInGpsCycle) - (gpsWeek * secondsInGpsWeek));
	}

	/**
	 * Construct GPSInstant from a POSIX Instant.
	 */
	public GPSInstant(Posix posix) {
		this(posix.secondsPosixEpoch() - Epoch.GPS.secondsPosixEpoch(), posix
				.getNanos());
	}

	/**
	 * Construct GPSInstant from a T60Instant.
	 */
	public GPSInstant(T60Instant instant) {
		this((instant.asLong() / 100000000L) - GPS_TO_LILIAN,
				(instant.asLong() % 100000000L) * 100L);
	}

	/**
	 * Return number of seconds since beginning of epoch.
	 * 
	 * @return
	 */
	public long getSecondsSinceEpoch() {
		return seconds;
	}

	/**
	 * Return fractional seconds of current second.
	 * 
	 * @return
	 */
	public long getNanos() {
		return nanos;
	}

	/**
	 * Return absolute number of weeks elapsed since beginning of GPS epoch.
	 * 
	 * @return
	 */
	public int getAbsoluteWeek() {
		return absoluteWeek;
	}

	/**
	 * Return week number in a 10-bit GPS cycle.
	 * 
	 * @return
	 */
	public int cycle() {
		return cycle;
	}

	/**
	 * Return week number in current 10-bit GPS cycle.
	 * 
	 * @return
	 */
	public int getGPSWeek() {
		return gpsWeek;
	}

	/**
	 * Return seconds elapsed in the current GPS week.
	 * 
	 * @return
	 */
	public int getSecondsOfGPSWeek() {
		return secondsOfGpsWeek;
	}

	/**
	 * Parses a string to create a GPSInstant. Format is:
	 * 
	 * (seconds-from-epoch).(fractional)GPS
	 * 
	 * where parentheses are not included.
	 * 
	 * @param string
	 * @return
	 * @throws ParseException
	 */
	public static GPSInstant parse(String string) throws ParseException {
		Matcher m = pattern.matcher(string);
		if (!m.matches()) {
			throw new ParseException("Invalid format for TAInstant given; \""
					+ string + "\"", 0);
		}
		long seconds = Long.parseLong(m.group(1));
		long nanos = Long.parseLong(m.group(2));
		return new GPSInstant(seconds, nanos);
	}

	/**
	 * Output string. Format is:
	 * 
	 * (seconds-from-epoch).(fractional)GPS
	 * 
	 * where parentheses are not included.
	 */
	public String toString() {
		return Long.toString(seconds) + "." + Long.toString(nanos) + "GPS";
	}

	/**
	 * Creates a "smeared" time UTC Instant with the SLS-1000 adjustment.
	 * 
	 * @return
	 */
	public org.joda.time.Instant toInstant() {
		// todo: fix this in java8 to support higher precision
		long posixSeconds = seconds + Epoch.TAI.secondsPosixEpoch();
		return new org.joda.time.Instant(posixSeconds * 1000L);
	}

	/**
	 * Creates a new TAIInstant.
	 * 
	 * @return
	 */
	public TAIInstant toTAIInstant() {
		long delta = Epoch.GPS.secondsPosixEpoch()
				- Epoch.TAI.secondsPosixEpoch() + 19;
		long taiSeconds = seconds + delta;
		return new TAIInstant(taiSeconds, nanos);
	}

	/**
	 * Creates a "smeared" time UTC Instant in the T60 format. In addition to
	 * the SLS-1000 adjustment, this method also is lossy in resolution.
	 * 
	 * @return
	 */
	public T60Instant toT60Instant() {
		long secondsTicks = (seconds + GPS_TO_LILIAN) * 10000000L;
		long nanosTicks = (nanos / 100L);
		return new T60Instant(secondsTicks + nanosTicks);
	}

	public int compareTo(GPSInstant that) {
		if (seconds < that.seconds)
			return -1;
		if (seconds > that.seconds)
			return 1;
		if (nanos < that.nanos)
			return -1;
		if (nanos > that.nanos)
			return 1;
		return 0;
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException ex) {
			return null;
		}
	}

	@Override
	public boolean equals(Object that) {
		if (!(that instanceof GPSInstant)) {
			return false;
		}
		return compareTo((GPSInstant) that) == 0;
	}

}