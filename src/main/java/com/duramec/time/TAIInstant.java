package com.duramec.time;

import java.io.Serializable;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TAIInstant implements Comparable<TAIInstant>, Serializable {

	private static final long serialVersionUID = 8092683329218735777L;

	/**
	 * Regular expression pattern describing .toString and parsing format of
	 * this class. Format is:
	 * 
	 * (seconds-from-epoch).(fractional)TAI
	 * 
	 * where parentheses are not included.
	 */
	private static Pattern pattern = Pattern
			.compile("(\\d{1,12})\\.(\\d{3,15})TAI");

	/**
	 * Seconds since beginning of TAI epoch.
	 */
	private final long seconds;

	/**
	 * Fractional remainder of seconds up to nanosecond precision.
	 */
	private final long nanos;

	/**
	 * TAIInstant constructor.
	 * 
	 * @param seconds
	 * @param nanos
	 */
	public TAIInstant(long seconds, long nanos) {
		this.seconds = seconds;
		this.nanos = nanos;
	}

	public TAIInstant(Posix posix) {
		long taiSeconds = posix.secondsPosixEpoch()
				- Epoch.TAI.secondsPosixEpoch();
		long offset = LeapTable.offset(posix);
		this.seconds = taiSeconds + offset;
		this.nanos = posix.getNanos();
	}

	// create a JulianInstant constructor

	/**
	 * Creates a new GPSInstant that is at the same absolute time.
	 * 
	 * @return
	 */
	public GPSInstant toGPSInstant() {
		long secondsGPS = seconds - Epoch.GPS.secondsPosixEpoch()
				+ Epoch.TAI.secondsPosixEpoch() - 19;
		return new GPSInstant(secondsGPS, nanos);
	}

	/**
	 * Construct a TAIInstant from a given POSIX offset and fractional time.
	 * 
	 * 
	 * Note: This method will not convert Posix seconds at a '+' leap second
	 * correctly as there is no way to disambuguate whether it is in the 59 or
	 * 60 second place. If it is a 60 second leap, it will convert over to
	 * second 0 of the next minute, just as is done normally.
	 * 
	 * @param seconds
	 * @param nanos
	 * @return
	 */
	/*
	 * public static TAIInstant fromPosix(long seconds, long nanos) { long
	 * taiSeconds = seconds - Epoch.TAI.asLong(); long offset =
	 * LeapTable.offset(seconds); return new TAIInstant(taiSeconds + offset,
	 * nanos); }
	 */

	/**
	 * Create a UTC Instant from this TAIInstant
	 * 
	 * @return
	 */
	/*
	 * public org.joda.time.Instant toInstant() { long utcSeconds = seconds +
	 * Epoch.TAI.asLong(); long offset = LeapTable.offset(utcSeconds); long
	 * utcMillis = (utcSeconds - offset) * 1000; return new
	 * org.joda.time.Instant(utcMillis); }
	 */

	/**
	 * Return whole seconds since TAI epoch.
	 * 
	 * @return
	 */
	public long getSecondsSinceEpoch() {
		return seconds;
	}

	/**
	 * Return fractional seconds within second.
	 * 
	 * @return
	 */
	public long getNanos() {
		return nanos;
	}

	/**
	 * Output string. Format is:
	 * 
	 * (seconds-from-epoch).(fractional)TAI
	 * 
	 * where parentheses are not included.
	 */
	@Override
	public String toString() {
		return Long.toString(seconds) + "." + Long.toString(nanos) + "TAI";
	}

	/**
	 * Parses a string to create a TAIInstant. Format is:
	 * 
	 * (seconds-from-epoch).(fractional)TAI
	 * 
	 * where parentheses are not included.
	 * 
	 * @param string
	 * @return
	 * @throws ParseException
	 */
	public static TAIInstant parse(String string) throws ParseException {
		Matcher m = pattern.matcher(string);
		if (!m.matches()) {
			throw new ParseException("Invalid format for TAInstant given; \""
					+ string + "\"", 0);
		}
		long seconds = Long.parseLong(m.group(1));
		long nanos = Long.parseLong(m.group(2));
		return new TAIInstant(seconds, nanos);
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
	public boolean equals(Object obj) {
		if (!(obj instanceof TAIInstant)) {
			return false;
		}
		return compareTo((TAIInstant) obj) == 0;
	}

	public int compareTo(TAIInstant that) {
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

}