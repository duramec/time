package com.duramec.time.test

import org.scalatest.{ FunSuite, ShouldMatchers }
import com.duramec.time._

class TAIInstantSuite extends FunSuite with ShouldMatchers {

  val testInstant = new TAIInstant(1234567890L, 328328328L)

  val testInstantString = "1234567890.328328328TAI"

  test ("is representable as a string") {
    expectResult (testInstantString) {
      testInstant.toString()
    }
  }

  test ("is parsed correctly") {
    expectResult (testInstant) {
      TAIInstant.parse(testInstantString)
    }
  }

  test ("compares correctly") {
    val instant1 = new TAIInstant(1234567890L, 0);
    val instant2 = new TAIInstant(1234567890L, 1111111L);
    expectResult (-1) {
      instant1.compareTo(instant2)
    }
    val instant3 = new TAIInstant(1234567890L, 1111111L);
    expectResult (0) {
      instant2.compareTo(instant3)
    }
    expectResult (1) {
      instant2.compareTo(instant1)
    }
  }

  test ("the TAI epoch in the Posix frame is negative") {
    expectResult (true) {
      Epoch.TAI.secondsPosixEpoch < 0
    }
  }

  test ("difference between GPS and TAI epoch is 8040 Julian Days") {
    expectResult (86400 * 8040) {
      Epoch.GPS.secondsPosixEpoch - Epoch.TAI.secondsPosixEpoch
    }
  }

  test ("GPS dates always are 19 seconds behind TAI in UTC") {
    val gps = new GPSInstant(Epoch.GPS.secondsPosixEpoch, 0)
    val tai = gps.toTAIInstant()
    expectResult (19) {
      val julianDaysOffset = 8040L
      val daysOffsetSeconds = julianDaysOffset * 86400L
      val taiSeconds = tai.getSecondsSinceEpoch() - daysOffsetSeconds
      val gpsSeconds = gps.getSecondsSinceEpoch()
      taiSeconds - gpsSeconds
    }
  }

  test ("converting from GPS to TAI and back yields same result") {
    val gps = new GPSInstant(Epoch.GPS.secondsPosixEpoch, 0)
    val inTai = gps.toTAIInstant()
    val gpsTwo = inTai.toGPSInstant()
    expectResult(true) {
      gps == gpsTwo
    }
  }

  def testConversion(yr: Int, mon: Int, day: Int, hr: Int, min: Int, s: Int, string: String) {
    val posix = new Posix(yr, mon, day, hr, min, s)
    val tai = new TAIInstant(posix)
  }

  test ("properly converts to UTC Instants for a range of times") {
    LeapTable.load("./tzdata/leapseconds");
    testConversion(1970, 1, 1, 0, 0, 0, "1970-01-01T00:00:00.000Z")
    testConversion(1972, 6, 30, 23, 59, 59, "1972-06-30T23:59:59.000Z")
    testConversion(1972, 6, 30, 23, 59, 60, "1972-07-01T00:00:00.000Z")
    testConversion(1972, 7, 1, 0, 0, 0, "1972-07-01T00:00:00.000Z")
    testConversion(2008, 12, 31, 23, 59, 59, "2008-12-31T23:59:59.000Z")
    testConversion(2008, 12, 31, 23, 59, 60, "2009-01-01T00:00:00.000Z")
    testConversion(2009, 1, 1, 0, 0, 0, "2009-01-01T00:00:00.000Z")
  }

}