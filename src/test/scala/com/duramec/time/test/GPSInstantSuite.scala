package com.duramec.time.test

import com.duramec.time._
import org.scalatest.FunSuite

class GpsInstantSuite extends FunSuite {

  test ("compares correctly") {
    val instant1 = new GPSInstant(1234567890L, 0);
    val instant2 = new GPSInstant(1234567890L, 1111111L);
    expectResult (-1) {
      instant1.compareTo(instant2)
    }
    val instant3 = new GPSInstant(1234567890L, 1111111L);
    expectResult (0) {
      instant2.compareTo(instant3)
    }
    expectResult (1) {
      instant2.compareTo(instant1)
    }
  }

  test ("has 0 seconds at GPS epoch when created with Posix class") {
    val gps = new GPSInstant(Epoch.GPS)
    expectResult (0) {
      gps.getSecondsSinceEpoch()
    }
  }

  // todo: add leap second checking on this and second rollover
  test ("has rollover on 1999-08-22 00:00") {
    val before = new GPSInstant(new Posix(1999, 8, 21, 23, 59, 59));
    expectResult (1023) {
      before.getGPSWeek()
    }
    expectResult (1023) {
      before.getAbsoluteWeek()
    }
    val during = new GPSInstant(new Posix(1999, 8, 22));
    expectResult (0) {
      during.getGPSWeek()
    }
    expectResult (1024) {
      during.getAbsoluteWeek()
    }
    val after = new GPSInstant(new Posix(1999, 8, 22, 0, 0, 0, 1));
    expectResult (0) {
      during.getGPSWeek()
    }
    expectResult (1024) {
      during.getAbsoluteWeek()
    }
  }

  test ("has second rollover on 2019-04-07 00:00") {
    val before = new GPSInstant(new Posix(2019, 4, 6, 23, 59, 59))
    expectResult (1023) {
      before.getGPSWeek()
    }
    expectResult (2047) {
      before.getAbsoluteWeek()
    }
    val during = new GPSInstant(new Posix(2019, 4, 7))
    expectResult (0) {
      during.getGPSWeek()
    }
    expectResult (2048) {
      during.getAbsoluteWeek()
    }
    val after = new GPSInstant(new Posix(2019, 4, 7, 0, 0, 0, 1))
    expectResult (0) {
      after.getGPSWeek()
    }
    expectResult (2048) {
      after.getAbsoluteWeek()
    }
  }

  /*
  
  
  test ("can slew a GPS Instant to an appropriate T60 Instant within leap boundary") {
    fail()
  }
  
  test ("can handle negative leap seconds") {
    fail()
  }*/

}