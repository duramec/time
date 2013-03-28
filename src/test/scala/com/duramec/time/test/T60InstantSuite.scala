package com.duramec.time.test

import com.duramec.time.T60Instant
import org.scalatest.FunSuite

class T60InstantSuite extends FunSuite {

  test ("minimum valid for 60-bit time") {
    val inst = new T60Instant(0L)
    expectResult ("1582-10-15T00:00:00.000000000Z") {
      inst.toString
    }
  }

  test ("truncates at tick precision") {
    val text = "1582-10-15T00:00:00.999999999Z"
    val inst = T60Instant.parse(text)
    expectResult ("1582-10-15T00:00:00.999999900Z") {
      inst.toString
    }
  }

  test ("various precisions near boundary") {
    val text1 = "1582-10-15T00:00:00.999000000Z"
    val inst1 = T60Instant.parse(text1)
    expectResult (text1) {
      inst1.toString
    }
    val text2 = "1582-10-15T00:00:00.000000100Z"
    val inst2 = T60Instant.parse(text2)
    expectResult (text2) {
      inst2.toString
    }
    val text3 = "1582-10-15T00:00:00.999999000Z"
    val inst3 = T60Instant.parse(text3)
    expectResult (text3) {
      inst3.toString
    }
  }

  /* test ("maximum valid for 60-bit time") {
    fail()
  }*/

  test ("no off-by-one millisecond problem") {
    val text = "2012-07-04T20:14:34.193000000Z"
    val inst = T60Instant.parse(text)
    expectResult (text) {
      inst.toString()
    }
  }

  /*
  test ("implements UTC-SLS correctly") {
    fail()
  }
  

  test ("100 nanosecond precision") {
    fail()
  }
  
  test ("microsecond precision") {
    fail()
  }
  
  test ("parses at millisecond level") {
    fail()
  }
  
  test ("parses at nanosecond precision") {
    fail()
  }
  
  test ("parse requires >= millisecond precision") {
    fail()
  }
  
  test ("comparison of Instant works") {
    fail()
  }*/
}