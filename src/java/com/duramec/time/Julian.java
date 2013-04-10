package com.duramec.time;

public class Julian {

	/**
	 * The Julian date epoch used here is not the full Julian period, but the
	 * one devised by NASA in 1979 known as Truncated Julian Date (TJD).
	 * Notably, it begins its date at midnight and not midday.
	 */
	public static final long truncated(int year, int month, int day) {
		long y = year;
		long m = month;
		long d = day;
		long L = (long) Math.ceil((m - 14.0) / 12.0);
		long jd = (d - 32075) + (1461 * (y + 4800 + L) / 4)
				+ (367 * (m - 2 - L * 12) / 12)
				- (3 * ((y + 4900 + L) / 100) / 4);
		return jd - 2440001;
	}

	public static final long truncated(Posix posix) {
		return truncated(posix.getYear(), posix.getMonth(), posix.getDay());
	}

}