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
package org.scalatest

import org.scalatest.matchers.ShouldMatchers

class SeveredStackTracesSpec extends FunSpec with ShouldMatchers with SeveredStackTraces {

  val baseLineNumber = 22

  describe("A severed TestFailedException") {

    it("should give the proper line on fail()") {
      try {
        fail()
      }
      catch {
        case e: TestFailedException =>
          e.failedCodeFileNameAndLineNumberString match {
            case Some(s) =>
              s should equal ("SeveredStackTracesSpec.scala:" + (baseLineNumber + 6))
              checkFileNameAndLineNumber(e, s)
            case None => fail("fail() didn't produce a file name and line number string: " + e.failedCodeFileNameAndLineNumberString, e)
          }
          e.failedCodeStackDepth should equal (4)
        case e =>
          fail("fail() didn't produce a TestFailedException", e)
      }
    }

    it("should give the proper line on fail(\"message\")") {
      try {
        fail("some message")
      }
      catch {
        case e: TestFailedException =>
          e.failedCodeFileNameAndLineNumberString match {
            case Some(s) =>
              s should equal ("SeveredStackTracesSpec.scala:" + (baseLineNumber + 24))
              checkFileNameAndLineNumber(e, s)
            case None => fail("fail(\"some message\") didn't produce a file name and line number string", e)
          }
          e.failedCodeStackDepth should equal (4)
        case e =>
          fail("fail(\"some message\") didn't produce a TestFailedException", e)
      }
    }

    it("should give the proper line on fail(throwable)") {
      try {
        fail(new RuntimeException)
      }
      catch {
        case e: TestFailedException =>
          e.failedCodeFileNameAndLineNumberString match {
            case Some(s) =>
              s should equal ("SeveredStackTracesSpec.scala:" + (baseLineNumber + 42))
              checkFileNameAndLineNumber(e, s)
            case None => fail("fail(throwable) didn't produce a file name and line number string", e)
          }
          e.failedCodeStackDepth should equal (4)
        case e =>
          fail("fail(throwable) didn't produce a TestFailedException", e)
      }
    }

    it("should give the proper line on fail(\"some message\", throwable)") {
      try {
        fail("some message", new RuntimeException)
      }
      catch {
        case e: TestFailedException =>
          e.failedCodeFileNameAndLineNumberString match {
            case Some(s) =>
              s should equal ("SeveredStackTracesSpec.scala:" + (baseLineNumber + 60))
              checkFileNameAndLineNumber(e, s)
            case None => fail("fail(\"some message\", throwable) didn't produce a file name and line number string", e)
          }
          e.failedCodeStackDepth should equal (4)
        case e =>
          fail("fail(\"some message\", throwable) didn't produce a TestFailedException", e)
      }
    }

    it("should give the proper line on assert(false)") {
      try {
        assert(false)
      }
      catch {
        case e: TestFailedException =>
          e.failedCodeFileNameAndLineNumberString match {
            case Some(s) =>
              s should equal ("SeveredStackTracesSpec.scala:" + (baseLineNumber + 78))
              checkFileNameAndLineNumber(e, s)
            case None => fail("assert(false) didn't produce a file name and line number string", e)
          }
          e.failedCodeStackDepth should equal (4)
        case e =>
          fail("assert(false) didn't produce a TestFailedException", e)
      }
    }

    it("should give the proper line on assert(false, \"some message\")") {
      try {
        assert(false, "some message")
      }
      catch {
        case e: TestFailedException =>
          e.failedCodeFileNameAndLineNumberString match {
            case Some(s) =>
              s should equal ("SeveredStackTracesSpec.scala:" + (baseLineNumber + 96))
              checkFileNameAndLineNumber(e, s)
            case None => fail("assert(false, \"some message\") didn't produce a file name and line number string", e)
          }
          e.failedCodeStackDepth should equal (4)
        case e =>
          fail("assert(false, \"some message\") didn't produce a TestFailedException", e)
      }
    }

    it("should give the proper line on assert(1 === 2)") {
      try {
        assert(1 === 2)
      }
      catch {
        case e: TestFailedException =>
          e.failedCodeFileNameAndLineNumberString match {
            case Some(s) =>
              s should equal ("SeveredStackTracesSpec.scala:" + (baseLineNumber + 114))
              checkFileNameAndLineNumber(e, s)
            case None => fail("assert(1 === 2) didn't produce a file name and line number string", e)
          }
          e.failedCodeStackDepth should equal (4)
        case e =>
          fail("assert(1 === 2) didn't produce a TestFailedException", e)
      }
    }

    it("should give the proper line on assert(1 === 2, \"some message\")") {
      try {
        assert(1 === 2, "some message")
      }
      catch {
        case e: TestFailedException =>
          e.failedCodeFileNameAndLineNumberString match {
            case Some(s) =>
              s should equal ("SeveredStackTracesSpec.scala:" + (baseLineNumber + 132))
              checkFileNameAndLineNumber(e, s)
            case None => fail("assert(1 === 2, \"some message\") didn't produce a file name and line number string", e)
          }
          e.failedCodeStackDepth should equal (4)
        case e =>
          fail("assert(1 === 2, \"some message\") didn't produce a TestFailedException", e)
      }
    }

    it("should give the proper line on expect(1) { 2 }") {
      try {
        expect(1) { 2 }
      }
      catch {
        case e: TestFailedException =>
          e.failedCodeFileNameAndLineNumberString match {
            case Some(s) =>
              s should equal ("SeveredStackTracesSpec.scala:" + (baseLineNumber + 150))
              checkFileNameAndLineNumber(e, s)
            case None => fail("expect(1) { 2 } didn't produce a file name and line number string", e)
          }
          e.failedCodeStackDepth should equal (4)
        case e =>
          fail("expect(1) { 2 } didn't produce a TestFailedException", e)
      }
    }

    it("should give the proper line on expect(1, \"some message\") { 2 }") {
      try {
        expect(1, "some message") { 2 }
      }
      catch {
        case e: TestFailedException =>
          e.failedCodeFileNameAndLineNumberString match {
            case Some(s) =>
              s should equal ("SeveredStackTracesSpec.scala:" + (baseLineNumber + 168))
              checkFileNameAndLineNumber(e, s)
            case None => fail("expect(1, \"some message\") { 2 } didn't produce a file name and line number string", e)
          }
          e.failedCodeStackDepth should equal (4)
        case e =>
          fail("expect(1, \"some message\") { 2 } didn't produce a TestFailedException", e)
      }
    }

    it("should give the proper line on intercept[IllegalArgumentException] {}") {
      try {
        intercept[IllegalArgumentException] {}
      }
      catch {
        case e: TestFailedException =>
          e.failedCodeFileNameAndLineNumberString match {
            case Some(s) =>
              s should equal ("SeveredStackTracesSpec.scala:" + (baseLineNumber + 186))
              checkFileNameAndLineNumber(e, s)
            case None => fail("intercept[IllegalArgumentException] {} didn't produce a file name and line number string", e)
          }
          e.failedCodeStackDepth should equal (4)
        case e =>
          fail("intercept[IllegalArgumentException] {} didn't produce a TestFailedException", e)
      }
    }

    it("should give the proper line on intercept[IllegalArgumentException] { throw new RuntimeException }") {
      try {
        intercept[IllegalArgumentException] { if (false) 1 else throw new RuntimeException }
      }
      catch {
        case e: TestFailedException =>
          e.failedCodeFileNameAndLineNumberString match {
            case Some(s) =>
              s should equal ("SeveredStackTracesSpec.scala:" + (baseLineNumber + 204))
              checkFileNameAndLineNumber(e, s)
            case None => fail("intercept[IllegalArgumentException] { throw new RuntimeException } didn't produce a file name and line number string", e)
          }
          e.failedCodeStackDepth should equal (4)
        case e =>
          fail("intercept[IllegalArgumentException] { throw new RuntimeException } didn't produce a TestFailedException", e)
      }
    }

    it("should give the proper line on 1 should be === 2") {
      try {
        1 should be === 2
      }
      catch {
        case e: TestFailedException =>
          e.failedCodeFileNameAndLineNumberString match {
            case Some(s) =>
              if (s != ("SeveredStackTracesSpec.scala:" + (baseLineNumber + 222))) {
                fail("s was: " + s, e)
              }
              checkFileNameAndLineNumber(e, s)
            case None => fail("1 should be === 2 didn't produce a file name and line number string", e)
          }
          e.failedCodeStackDepth should equal (4)
        case e =>
          fail("1 should be === 2 didn't produce a TestFailedException", e)
      }
    }

    it("should give the proper line on evaluating {} should produce [IllegalArgumentException] {}") {
      try {
        evaluating {} should produce [IllegalArgumentException]
      }
      catch {
        case e: TestFailedException =>
          e.failedCodeFileNameAndLineNumberString match {
            case Some(s) => // s should equal ("SeveredStackTracesSpec.scala:" + (baseLineNumber + 204))
              if (s != ("SeveredStackTracesSpec.scala:" + (baseLineNumber + 242))) {
                fail("s was: " + s, e)
              }
              checkFileNameAndLineNumber(e, s)
            case None => fail("evaluating {} should produce [IllegalArgumentException] didn't produce a file name and line number string", e)
          }
          e.failedCodeStackDepth should equal (3)
        case e =>
          fail("evaluating {} should produce [IllegalArgumentException] didn't produce a TestFailedException", e)
      }
    }

    it("should give the proper line on evaluating { throw new RuntimeException } should produce [IllegalArgumentException]") {
      try {
        evaluating { if (false) 1 else throw new RuntimeException } should produce [IllegalArgumentException]
      }
      catch {
        case e: TestFailedException =>
          e.failedCodeFileNameAndLineNumberString match {
            case Some(s) =>
              s should equal ("SeveredStackTracesSpec.scala:" + (baseLineNumber + 262))
              checkFileNameAndLineNumber(e, s)
            case None => fail("evaluating { throw new RuntimeException } should produce [IllegalArgumentException] didn't produce a file name and line number string", e)
          }
          e.failedCodeStackDepth should equal (3)
        case e =>
          fail("evaluating { throw new RuntimeException } should produce [IllegalArgumentException] didn't produce a TestFailedException", e)
      }
    }

    it("should return the cause in both cause and getCause") {
      val theCause = new IllegalArgumentException("howdy")
      val tfe = new TestFailedException(Some("doody"), Some(theCause), 3)
      assert(tfe.cause.isDefined)
      assert(tfe.cause.get === theCause)
      assert(tfe.getCause == theCause)
    }

    it("should return None in cause and null in getCause if no cause") {
      val tfe = new TestFailedException(Some("doody"), None, 3)
      assert(tfe.cause.isEmpty)
      assert(tfe.getCause == null)
    }
  }
  private def checkFileNameAndLineNumber(e: TestFailedException, failedCodeFileNameAndLineNumberString: String) {
    val stackTraceElement = e.getStackTrace()(e.failedCodeStackDepth)
    val fileName = stackTraceElement.getFileName
    val lineNumber = stackTraceElement.getLineNumber
    failedCodeFileNameAndLineNumberString should equal (fileName + ":" + lineNumber)
  }
}
