package com.duramec.time;

import org.joda.time.DateTime;

public final class Posix {

	private int year;

	private int month;

	private int day;

	private int hour;

	private int minute;

	private int second;

	private long secondsEpoch;

	private long nanos;

	public Posix(long seconds) {
		this(seconds, 0);
	}

	public Posix(long seconds, long nanos) {
		this.secondsEpoch = seconds;
		DateTime dateTime = new DateTime(seconds * 1000L);
		this.year = dateTime.getYear();
		this.month = dateTime.getMonthOfYear();
		this.day = dateTime.getDayOfMonth();
		this.hour = dateTime.getHourOfDay();
		this.minute = dateTime.getMinuteOfHour();
		this.second = dateTime.getSecondOfMinute();
		this.nanos = nanos;
	}

	public Posix(int yr, int mon, int day) {
		this(yr, mon, day, 0, 0, 0, 0L);
	}

	public Posix(int yr, int mon, int day, int hr, int min, int s) {
		this(yr, mon, day, hr, min, s, 0L);
	}

	public Posix(int yr, int mon, int day, int hr, int min, int s, long nanos) {
		assert (mon >= 1 && mon <= 12);
		assert (day >= 1 && day <= 31);
		assert (hr >= 0 && hr < 24);
		assert (min >= 0 && min < 60);
		assert (s >= 0 && s <= 60); // allow leap second
		assert (nanos >= 0);
		this.year = yr;
		this.month = mon;
		this.day = day;
		this.hour = hr;
		this.minute = min;
		this.second = s;
		this.nanos = nanos;
		long days = Julian.truncated(yr, mon, day) - Epoch.POSIX_TJD;
		this.secondsEpoch = (days * 86400L) + (hr * 3600) + (min * 60) + s;
	}

	public long secondsPosixEpoch() {
		return secondsEpoch;
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

	public long getNanos() {
		return nanos;
	}

}