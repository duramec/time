package com.duramec.time;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class T60Instant implements Comparable<T60Instant>, Serializable,
		Cloneable {

	private static final DateTimeFormatter datetimeFormat = DateTimeFormat
			.forPattern("yyyy-MM-dd'T'HH':'mm':'ss").withZoneUTC();

	public static final T60Instant MIN = new T60Instant(0L);

	public static final T60Instant MAX = new T60Instant(0x0FFFFFFFFFFFFFFFL);

	public static final long lilianToPosixTicks = Epoch.LILIAN_TO_POSIX * 10000000L;

	private static final Pattern pattern = Pattern
			.compile("(\\d\\d\\d\\d-\\d\\d-\\d\\d"
					+ "[T ]\\d\\d:\\d\\d:\\d\\d)(?:[.](\\d{3,9}))[Z]");

	private long ticks;

	public T60Instant(byte[] bytes) {
		this(ByteBuffer.wrap(bytes).getLong());
	}

	public T60Instant(Posix posix) {
		this((posix.secondsPosixEpoch() * 10000000L)
				+ (posix.getNanos() / 100L));
	}

	public T60Instant(long ticks) {
		this.ticks = ticks;
	}

	public final long asLong() {
		return ticks;
	}

	private final long asSeconds() {
		return (ticks / 10000000L) - Epoch.LILIAN_TO_POSIX;
	}

	private static final long unixToT60(long ms) {
		return (ms * 10000L) + lilianToPosixTicks;
	}

	private static final long t60ToUnix(long ticks) {
		return (ticks - lilianToPosixTicks) / 10000L;
	}

	public final DateTime asDateTime(DateTimeZone zone) {
		long unix = t60ToUnix(asLong());
		return new DateTime(unix, zone);
	}

	public static final T60Instant parse(CharSequence text)
			throws ParseException {
		Matcher m = pattern.matcher(text);
		if (!m.matches()) {
			throw new ParseException("\"" + text
					+ "\" cannot be parsed as a strict RFC-3339 instant.", 0);
		}
		String dateTime = m.group(1);
		String nanoString = m.group(2);

		DateTime dt = DateTime.parse(dateTime, datetimeFormat);
		long ms = dt.getMillis();
		long millisTicks = unixToT60(ms);
		long nanosTicks = (parseNanos(nanoString) / 100L);
		long ticks = millisTicks + nanosTicks;

		if (ticks > MAX.ticks) { // check within range
			throw new ParseException("\"" + text
					+ "\" parses to a timestamp larger than supported "
					+ "by the T60 maximum range.", 0);
		}
		return new T60Instant(ticks);
	}

	private static long parseNanos(String s) {
		if (s == null) {
			return 0L;
		} else if (s.length() < 9) {
			return Long.parseLong(s + "000000000".substring(s.length()));
		} else {
			return Long.parseLong(s);
		}
	}

	@Override
	public final String toString() {
		long msRoundedToSecond = asSeconds() * 1000L;
		DateTime whole = new DateTime(msRoundedToSecond, DateTimeZone.UTC);
		String datetime = whole.toString(datetimeFormat);
		long wholeTicks = unixToT60(whole.getMillis());
		long fractionalTicks = (asLong() - wholeTicks);
		String fractional = String.format(".%07d00Z", fractionalTicks);
		return datetime + fractional;
	}

	private void readObject(ObjectInputStream in) throws IOException {
		ticks = in.readLong();
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeLong(ticks);
	}

	public byte[] getBytes() {
		ByteBuffer buf = ByteBuffer.allocate(8);
		buf.order(ByteOrder.BIG_ENDIAN);
		buf.putLong(asLong());
		return buf.array();
	}

	public static T60Instant convert(byte[] bytes) {
		return new T60Instant(bytes);
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
		if (!(obj instanceof T60Instant)) {
			return false;
		}
		return compareTo((T60Instant) obj) == 0;
	}

	public int compareTo(T60Instant inst) {
		if (ticks > inst.ticks)
			return 1;
		if (ticks < inst.ticks)
			return -1;
		return 0;
	}

}
