package com.duramec.time;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import com.duramec.time.LeapSecond.JumpDirection;

public class LeapTable {

	/**
	 * This number may become updated as time goes on. It is a basic check that
	 * there are some number of records in the leapsecond table.
	 */
	public static long minimumEntriesRequired = 25;

	/**
	 * A LeapSecond past any time in range. Should have offset equal to the very
	 * last offset in table.
	 */
	private static LeapSecond infinity;

	/**
	 * Disable the constructor. This is a static class.
	 */
	private LeapTable() {
	}

	/**
	 * Regular expression for single line of a leapseconds table. The format is:
	 * 
	 * Leap YEAR MONTH DAY HH:MM:SS CORR R/S
	 * 
	 * The "R/S" (Rolling/Stationary) bit is ignored, as there should never be
	 * an offset in the file determined in reference to Local Time. All properly
	 * formed leapseconds files should only have "S" for Stationary.
	 * 
	 */
	private static String regex = "^Leap\\s+(\\d{4})\\s+(\\w+)\\s+(\\d{2})\\s+(\\d{2}):(\\d{2}):(\\d{2})\\s+([+-]).*$";

	/**
	 * Compiled pattern for regex, employing MULTILINE option so that ^$ match
	 * at newline.
	 */
	private static Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);

	/**
	 * Table of all leap seconds.
	 */
	private static ArrayList<LeapSecond> table;

	/**
	 * Last leap second
	 */
	private static LeapSecond last;

	/**
	 * Get a clone of the table.
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<LeapSecond> getTableClone() {
		return (ArrayList<LeapSecond>) (table.clone());
	}

	/**
	 * The creation of an "infinite" leap second that is ahead of any time in
	 * range. This method should be given an offset which is equal to the last
	 * offset found in the table.
	 * 
	 * @param offset
	 * @return
	 */
	private static LeapSecond createInfinity(int offset) {
		return new LeapSecond(offset, 9999, 12, 31, 23, 59, 59,
				JumpDirection.FORWARD);
	}

	/**
	 * Pull in a whole file to String from a filename.
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
	private static String slurp(String file) throws FileNotFoundException,
			ParseException {
		return new Scanner(new File(file), "UTF-8").useDelimiter("\\A").next();
	}

	private static int stringMonthToInt(String month) throws ParseException {
		switch (month) {
		case "Jan":
			return 1;
		case "Feb":
			return 2;
		case "Mar":
			return 3;
		case "Apr":
			return 4;
		case "May":
			return 5;
		case "Jun":
			return 6;
		case "Jul":
			return 7;
		case "Aug":
			return 8;
		case "Sep":
			return 9;
		case "Oct":
			return 10;
		case "Nov":
			return 11;
		case "Dec":
			return 12;
		}
		throw new ParseException("Input string \"" + month
				+ "\" cannot be parsed into a month", 0);
	}

	/**
	 * Create a single LeapSecond (with given offset) from the currently
	 * regular-expression match group.
	 * 
	 * @param m
	 * @param offset
	 * @return
	 */
	private static LeapSecond createRecord(Matcher m, int lastOffset)
			throws ParseException {
		int year = Integer.parseInt(m.group(1));
		int month = stringMonthToInt(m.group(2));
		int day = Integer.parseInt(m.group(3));
		int hour = Integer.parseInt(m.group(4));
		int minute = Integer.parseInt(m.group(5));
		int second = Integer.parseInt(m.group(6));
		JumpDirection direction = null;
		int offset = lastOffset;
		if (m.group(7).equals("+")) {
			direction = JumpDirection.FORWARD;
			offset += 1;
		} else {
			direction = JumpDirection.BACKWARD;
			offset -= 1;
		}
		return new LeapSecond(offset, year, month, day, hour, minute, second,
				direction);
	}

	/**
	 * Load leapseconds table from file.
	 * 
	 * @param file
	 * @throws ParseException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static synchronized void load(String file) throws ParseException,
			FileNotFoundException, IOException {
		String text = slurp(file);
		Matcher m = pattern.matcher(text);
		ArrayList<LeapSecond> leapSeconds = new ArrayList<LeapSecond>();
		/**
		 * UTC was originally defined with an offset of 10 on January 1, 1972.
		 * The first leap second added to this offset by 1 on June 30, 1972.
		 */
		int lastOffset = 10;
		while (m.find()) {
			LeapSecond leap = createRecord(m, lastOffset);
			leapSeconds.add(leap);
			lastOffset = leap.getOffset();
		}
		/**
		 * Some minimal checking that the leap seconds file is not completely
		 * invalid.
		 */
		if (leapSeconds.size() < minimumEntriesRequired) {
			throw new ParseException(
					"leapseconds file does not have enough entries to be valid",
					0);
		}
		/**
		 * Perform a sanity-check sort on the LeapSeconds (which occurs based
		 * upon the underlying offset from the POSIX epoch) to ensure they are
		 * in the proper ordre and add the table to the static reference inside
		 * this class. Create a new "infinity" end of range to be used in
		 * searching algorithms.
		 */
		Collections.sort(leapSeconds);
		LeapTable.table = leapSeconds;
		LeapTable.infinity = createInfinity(lastOffset);
		LeapTable.last = leapSeconds.get(leapSeconds.size() - 1);
	}

	/**
	 * Number of seconds from reference until next leap second. If this number
	 * if negative, it means we are ahead of the last known leap second.
	 * 
	 * @param posixSeconds
	 * @return
	 */
	public static long secondsUntilFinalLeap(long posixSeconds) {
		return last.occursAtPosixSeconds() - posixSeconds;
	}

	// hmmm... i think this might be a bit fishy
	public static Boolean isLeapSecond(long posix) {
		ListIterator<LeapSecond> it = table.listIterator(table.size());
		while (it.hasPrevious()) {
			if (it.previous().occursAtPosixSeconds() == posix) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determine what the TAI-UTC offset is based upon a number of seconds in
	 * the POSIX frame.
	 * 
	 * @param posix
	 * @return
	 */
	public static int offset(Posix posix) {
		long posixSeconds = posix.secondsPosixEpoch();
		ListIterator<LeapSecond> it = table.listIterator(table.size());
		LeapSecond leapOnePast = infinity;
		LeapSecond leap = null;
		while (it.hasPrevious()) {
			leap = it.previous();
			if (posixSeconds >= leap.occursAtPosixSeconds()) {
				return leapOnePast.getOffset();
			}
			leapOnePast = leap;
		}
		return 10; // Reached before June 30, 1972
	}

}
