/*
 * Copyright 2001-2008 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scalatest.matchers

import org.scalatest._
import org.scalatest.prop.Checkers
import org.scalacheck._
import Arbitrary._
import Prop._

class ShouldEqualSpec extends FunSpec with ShouldMatchers with Checkers with ReturnsNormallyThrowsAssertion {

  // Checking for equality with "equal"
  describe("The equal token") {

    it("should do nothing when equal") {
      1 should equal (1)

      // objects should equal themselves
      check((s: String) => returnsNormally(s should equal (s)))
      check((i: Int) => returnsNormally(i should equal (i)))

      // a string should equal another string with the same value
      check((s: String) => returnsNormally(s should equal (new String(s))))
    }

    it("should do nothing when not equal and used with not") {
      1 should not { equal (2) }
      1 should not equal (2)

      // unequal objects should not equal each other
      check((s: String, t: String) => s != t ==> returnsNormally(s should not { equal (t) }))
      check((s: String, t: String) => s != t ==> returnsNormally(s should not equal (t)))
    }

    it("should do nothing when equal and used in a logical-and expression") {
      1 should (equal (1) and equal (2 - 1))
    }

    it("should do nothing when equal and used in multi-part logical expressions") {

        // Just to make sure these work strung together
        1 should (equal (1) and equal (1) and equal (1) and equal (1))
        1 should (equal (1) and equal (1) or equal (1) and equal (1) or equal (1))
        1 should (
            equal (1) and
            equal (1) or
            equal (1) and
            equal (1) or
            equal (1)
        )
    }

    it("should do nothing when equal and used in a logical-or expression") {
      1 should { equal (1) or equal (2 - 1) }
    }

    it("should do nothing when not equal and used in a logical-and expression with not") {
      1 should { not { equal (2) } and not { equal (3 - 1) }}
      1 should { not equal (2) and (not equal (3 - 1)) }
      1 should (not equal (2) and not equal (3 - 1))
    }

    it("should do nothing when not equal and used in a logical-or expression with not") {
      1 should { not { equal (2) } or not { equal (3 - 1) }}
      1 should { not equal (2) or (not equal (3 - 1)) }
      1 should (not equal (2) or not equal (3 - 1))
    }

    it("should throw an assertion error when not equal") {
      val caught1 = intercept[TestFailedException] {
        1 should equal (2)
      }
      assert(caught1.getMessage === "1 did not equal 2")

      // unequal objects used with "a should equal (b)" should throw an TestFailedException
      check((s: String, t: String) => s != t ==> throwsTestFailedException(s should equal (t)))

      val caught2 = intercept[TestFailedException] {
        1 should not (not equal (2))
      }
      assert(caught2.getMessage === "1 did not equal 2")
    }

    it("should throw an assertion error when equal but used with should not") {
      val caught1 = intercept[TestFailedException] {
        1 should not { equal (1) }
      }
      assert(caught1.getMessage === "1 equaled 1")

      val caught2 = intercept[TestFailedException] {
        1 should not equal (1)
      }
      assert(caught2.getMessage === "1 equaled 1")

      // the same object used with "a should not { equal (a) } should throw TestFailedException
      check((s: String) => throwsTestFailedException(s should not { equal (s) }))
      check((i: Int) => throwsTestFailedException(i should not { equal (i) }))
      check((s: String) => throwsTestFailedException(s should not equal (s)))
      check((i: Int) => throwsTestFailedException(i should not equal (i)))

      // two different strings with the same value used with "s should not { equal (t) } should throw TestFailedException
      check((s: String) => throwsTestFailedException(s should not { equal (new String(s)) }))
      check((s: String) => throwsTestFailedException(s should not equal (new String(s))))

      val caught3 = intercept[TestFailedException] {
        1 should not (not (not equal (1)))
      }
      assert(caught3.getMessage === "1 equaled 1")
    }

    it("should throw an assertion error when not equal and used in a logical-and expression") {
      val caught = intercept[TestFailedException] {
        1 should { equal (5) and equal (2 - 1) }
      }
      assert(caught.getMessage === "1 did not equal 5")
    }

    it("should throw an assertion error when not equal and used in a logical-or expression") {
      val caught = intercept[TestFailedException] {
        1 should { equal (5) or equal (5 - 1) }
      }
      assert(caught.getMessage === "1 did not equal 5, and 1 did not equal 4")
    }

    it("should throw an assertion error when equal and used in a logical-and expression with not") {

      val caught1 = intercept[TestFailedException] {
        1 should { not { equal (1) } and not { equal (3 - 1) }}
      }
      assert(caught1.getMessage === "1 equaled 1")

      val caught2 = intercept[TestFailedException] {
        1 should { not equal (1) and (not equal (3 - 1)) }
      }
      assert(caught2.getMessage === "1 equaled 1")

      val caught3 = intercept[TestFailedException] {
        1 should (not equal (1) and not equal (3 - 1))
      }
      assert(caught3.getMessage === "1 equaled 1")

      val caught4 = intercept[TestFailedException] {
        1 should { not { equal (2) } and not { equal (1) }}
      }
      assert(caught4.getMessage === "1 did not equal 2, but 1 equaled 1")

      val caught5 = intercept[TestFailedException] {
        1 should { not equal (2) and (not equal (1)) }
      }
      assert(caught5.getMessage === "1 did not equal 2, but 1 equaled 1")

      val caught6 = intercept[TestFailedException] {
        1 should (not equal (2) and not equal (1))
      }
      assert(caught6.getMessage === "1 did not equal 2, but 1 equaled 1")
    }

    it("should throw an assertion error when equal and used in a logical-or expression with not") {

      val caught1 = intercept[TestFailedException] {
        1 should { not { equal (1) } or not { equal (2 - 1) }}
      }
      assert(caught1.getMessage === "1 equaled 1, and 1 equaled 1")

      val caught2 = intercept[TestFailedException] {
        1 should { not equal (1) or { not equal (2 - 1) }}
      }
      assert(caught2.getMessage === "1 equaled 1, and 1 equaled 1")

      val caught3 = intercept[TestFailedException] {
        1 should (not equal (1) or not equal (2 - 1))
      }
      assert(caught3.getMessage === "1 equaled 1, and 1 equaled 1")
    }
    
    it("should put string differences in square bracket") {
      val caught1 = intercept[TestFailedException] { "dummy" should equal ("dunny") }
      caught1.getMessage should equal ("\"du[mm]y\" did not equal \"du[nn]y\"")
      
      val caught2 = intercept[TestFailedException] { "dummy" should be ("dunny") }
      caught2.getMessage should be ("\"du[mm]y\" was not equal to \"du[nn]y\"")
      
      val caught3 = intercept[TestFailedException] { "hi there mom" should be ===  ("high there mom") }
      caught3.getMessage should be ("\"hi[] there mom\" was not equal to \"hi[gh] there mom\"")
    }
    
    it("should not put string differences in square bracket") {
      val caught1 = intercept[TestFailedException] { "dummy" should not equal "dummy" }
      caught1.getMessage should equal ("\"dummy\" equaled \"dummy\"")
      
      val caught2 = intercept[TestFailedException] { "dummy" should not be "dummy" }
      caught2.getMessage should equal ("\"dummy\" was equal to \"dummy\"")
    }
  }
}
