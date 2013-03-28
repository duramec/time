package com.duramec.time;

import org.joda.time.DateTime;

public final class LeapSecond implements Comparable<LeapSecond>, Cloneable {

	public enum JumpDirection {
		BACKWARD, FORWARD
	}

	private final int offset;

	private final int year;

	private final int month;

	private final int day;

	private final int hour;

	private final int minute;

	private final int second;

	private final JumpDirection direction;

	private final long occursAtPosixSeconds;

	/**
	 * Note: Do not use the Java or Joda DateTime class here as the forward
	 * leap-second will exceed the boundaries of the 'second' attribute.
	 * 
	 * Note: Month numbering starts at 1, not 0.
	 * 
	 * Errors in the leap second table are so severe that we should crash this
	 * program if an assertion fails.
	 * 
	 */
	public LeapSecond(int offset, int year, int month, int day, int hour,
			int minute, int second, JumpDirection direction) {
		assert (year >= 1972);
		assert (month > 0);
		assert (day > 0 && day <= 31);
		assert (hour >= 0 && hour < 24);
		assert (second >= 0 && second <= 60);
		this.offset = offset;
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		this.direction = direction;
		/**
		 * The seconds are left off the DateTime constructor. This is in the
		 * event that the leap second is truncated from that constructor.
		 */
		DateTime dtPosix = new DateTime(year, month, day, hour, minute);
		long secondsAtMinutePosix = dtPosix.getMillis() / 1000L;
		this.occursAtPosixSeconds = secondsAtMinutePosix + second;
	}

	public long occursAtPosixSeconds() {
		return occursAtPosixSeconds;
	}

	public int getOffset() {
		return offset;
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDay() {
		return day;
	}

	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}

	public int getSecond() {
		return second;
	}

	public JumpDirection getDirection() {
		return direction;
	}

	@Override
	public int compareTo(LeapSecond that) {
		if (occursAtPosixSeconds < that.occursAtPosixSeconds)
			return -1;
		if (occursAtPosixSeconds > that.occursAtPosixSeconds)
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

}