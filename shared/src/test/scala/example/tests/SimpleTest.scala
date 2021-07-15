/*
 * Scala (https://www.scala-lang.org)
 *
 * Copyright EPFL and Lightbend, Inc.
 *
 * Licensed under Apache License 2.0
 * (http://www.apache.org/licenses/LICENSE-2.0).
 *
 * See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 */

package example.tests

import verify.BasicTestSuite
import scala.concurrent.Future

object SimpleTest extends BasicTestSuite {
  test("ignored test") {
    ignore()
  }

  test("ignore test with reason") {
    ignore("test was ignored with a message")
  }

  test("canceled test") {
    cancel()
  }

  test("canceled test with reason") {
    cancel("test was canceled with a message")
  }

  test("simple assert") {
    def hello: String = "hello"
    assert(hello == "hello")
  }

  test("assert with hint") {
    def hello: String = "hello"
    assert(hello == "hello", "assertion with hint is failing")
  }

  // test("failing test") {
  //   case class Person(name: String = "Fred", age: Int = 42) {
  //     def say(words: String*) = words.mkString(" ")
  //   }
  //   assert(Person().say("ping", "poing") == "pong pong")
  // }

  test("assert equals with nulls") {
    val s: String = null

    intercept[AssertionError] {
      assert(s == "dummy")
    }
    ()
  }

  test("intercept") {
    class DummyException(message: String) extends RuntimeException(message)
    def test = 1

    val ex = intercept[DummyException] {
      if (test != 2) throw new DummyException("value was different to 2")
    }
    assertEquals("value was different to 2", ex.getMessage)
  }

  testAsync("asynchronous test") {
    import scala.concurrent.ExecutionContext.Implicits.global

    Future(1).map(_ + 1).map { result =>
      assert(result == 2)
    }
  }

  test("intercept failure") {
    class DummyException extends RuntimeException

    intercept[AssertionError] {
      intercept[DummyException] {
        def hello(x: Int) = x + 1
        if (hello(1) != 2) throw new DummyException
      }
    }
    ()
  }

  test("fail()") {
    def x = 1
    intercept[AssertionError] { if (x == 1) fail() }
    ()
  }

  test("fail(reason)") {
    def x = 1
    val isSuccess = try {
      if (x == 1) fail("dummy")
      false
    } catch {
      case ex: AssertionError =>
        ex.getMessage == "dummy"
    }

    assert(isSuccess)
  }
}
