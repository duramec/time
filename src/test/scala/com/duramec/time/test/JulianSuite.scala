package com.duramec.time

import org.scalatest.FunSuite

class JulianSuite extends FunSuite {

  test ("gives correct TJD for Lilian epoch") {
    expectResult (-140840) {
      Julian.truncated(Epoch.LILIAN)
    }
  }

  test ("gives correct TJD for Posix epoch") {
    expectResult (587) {
      Julian.truncated(Epoch.POSIX)
    }
  }

  test ("gives correct TJD for GPS epoch") {
    expectResult (4244) {
      Julian.truncated(Epoch.GPS)
    }
  }

  test ("gives correct TJD for TAI epoch") {
    expectResult (-3796) {
      Julian.truncated(Epoch.TAI)
    }
  }

}