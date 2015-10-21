package uk.camsw.rxscala.dsl

import org.scalatest.{Matchers, FunSpec}

class HelloTest
  extends FunSpec
  with Matchers {

  describe("Anything") {
    it("should fail") {
      false shouldBe true
    }
  }
}
