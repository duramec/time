package com.duramec.time.test

import org.scalatest.{ FunSuite, PrivateMethodTester }
import com.duramec.time._
import com.duramec.time.LeapSecond.JumpDirection
import java.util.{ ArrayList, ListIterator }

class LeapSecondSuite extends FunSuite with PrivateMethodTester {

  var count: Int = 0

  def ensureRecord(year: Int, month: Int, day: Int,
                   hour: Int, minute: Int, second: Int, correlation: JumpDirection)(implicit iterator: ListIterator[LeapSecond]) {
    val record = iterator.next()
    expectResult(year) { record.getYear() }
    expectResult(month) { record.getMonth() }
    expectResult(day) { record.getDay() }
    expectResult(hour) { record.getHour() }
    expectResult(minute) { record.getMinute() }
    expectResult(second) { record.getSecond() }
    expectResult(correlation) { record.getDirection() }
    count = count + 1
  }

  /**
    * Note: this test is designed to fail every time the leapseconds file
    * is updated with new entries.  This is to ensure a new unit test is
    * written to verify the table is correct prior to compilation.
    *
    * Update this unit test as necessary, and do so manually.  Do not
    * have a script automatically update it.
    *
    */
  test ("correctly parses leapseconds table") {
    LeapTable.load("./tzdata/leapseconds")
    import JumpDirection._
    val table = LeapTable.getTableClone()
    implicit val iterator = table.listIterator()
    ensureRecord(1972, 6, 30, 23, 59, 60, FORWARD)
    ensureRecord(1972, 12, 31, 23, 59, 60, FORWARD)
    ensureRecord(1973, 12, 31, 23, 59, 60, FORWARD)
    ensureRecord(1974, 12, 31, 23, 59, 60, FORWARD)
    ensureRecord(1975, 12, 31, 23, 59, 60, FORWARD)
    ensureRecord(1976, 12, 31, 23, 59, 60, FORWARD)
    ensureRecord(1977, 12, 31, 23, 59, 60, FORWARD)
    ensureRecord(1978, 12, 31, 23, 59, 60, FORWARD)
    ensureRecord(1979, 12, 31, 23, 59, 60, FORWARD)
    ensureRecord(1981, 6, 30, 23, 59, 60, FORWARD)
    ensureRecord(1982, 6, 30, 23, 59, 60, FORWARD)
    ensureRecord(1983, 6, 30, 23, 59, 60, FORWARD)
    ensureRecord(1985, 6, 30, 23, 59, 60, FORWARD)
    ensureRecord(1987, 12, 31, 23, 59, 60, FORWARD)
    ensureRecord(1989, 12, 31, 23, 59, 60, FORWARD)
    ensureRecord(1990, 12, 31, 23, 59, 60, FORWARD)
    ensureRecord(1992, 6, 30, 23, 59, 60, FORWARD)
    ensureRecord(1993, 6, 30, 23, 59, 60, FORWARD)
    ensureRecord(1994, 6, 30, 23, 59, 60, FORWARD)
    ensureRecord(1995, 12, 31, 23, 59, 60, FORWARD)
    ensureRecord(1997, 6, 30, 23, 59, 60, FORWARD)
    ensureRecord(1998, 12, 31, 23, 59, 60, FORWARD)
    ensureRecord(2005, 12, 31, 23, 59, 60, FORWARD)
    ensureRecord(2008, 12, 31, 23, 59, 60, FORWARD)
    ensureRecord(2012, 6, 30, 23, 59, 60, FORWARD)
    /**
      * Make sure we have tested all of the records in the file
      */
    expectResult (false) {
      iterator.hasNext()
    }
    /**
      * Ensure the correct number is present in the runtime minimum
      * of the the table.
      */
    expectResult (count) {
      LeapTable.minimumEntriesRequired
    }
  }

  /*
  test ("handles staggering positive and negative leapseconds") {
    fail()
  }
  
  test ("minimum number of required records set correctly") {
    fail()
  }
  
  test ("can identify if a GPS Instant is within a leap second boundary") {
    fail()
  }
  
  test ("can identify if a T60 Instant is within a leap second boundary") {
    fail()
  }
 
*/
}