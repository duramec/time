package com.duramec.time;

public final class Epoch {

	private Epoch() {
	}

	/**
	 * These are the dates of the Epoch reference in POSIX for common time
	 * formats. Note that the following restrictions apply:
	 * 
	 * ⇒ Only valid for dates after the GPS epoch (1980/1/6 UTC)
	 * 
	 * ⇒ This was UTC at the time of the epoch creation without leap seconds
	 * 
	 */
	public static final Posix GPS = new Posix(1980, 1, 6);

	public static final Posix LILIAN = new Posix(1582, 10, 15);

	public static final Posix TAI = new Posix(1958, 1, 1);

	public static final Posix JULIAN = new Posix(1968, 5, 1);

	public static final Posix POSIX = new Posix(1970, 1, 1);

	/**
	 * Note: Keep this as 587 instead of calling the Julian conversion function
	 * on the POSIX element above. There is some weird bug in the order of the
	 * constructors that has the following value set to 0 for a while if you try
	 * to construct it with the POSIX variable.
	 */
	public static final long POSIX_TJD = 587;

	public static final long LILIAN_TO_POSIX = 12219292800L;

}
