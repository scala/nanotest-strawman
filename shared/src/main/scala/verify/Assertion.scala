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

package verify

import scala.reflect.ClassTag
import verify.sourcecode.SourceLocation
import verify.asserts.PowerAssert

trait Assertion extends asserts.AssertEquals[Unit] {
  override protected def stringAssertEqualsListener = PowerAssert.stringAssertEqualsListener
  lazy val assert: PowerAssert = new PowerAssert()

  def intercept[E <: Throwable: ClassTag](callback: => Unit)(implicit pos: SourceLocation): Throwable = {

    val E = implicitly[ClassTag[E]]
    try {
      callback
      val name = E.runtimeClass.getName
      throw new InterceptException(s"expected a $name to be thrown", pos)
    } catch {
      case ex: InterceptException =>
        throw new AssertionError(ex.getMessage)
      case ex: Throwable if E.runtimeClass.isInstance(ex) =>
         ex
    }
  }

  def cancel()(implicit pos: SourceLocation): Unit =
    throw new CanceledException(None, Some(pos))

  def cancel(reason: String)(implicit pos: SourceLocation): Unit =
    throw new CanceledException(Some(reason), Some(pos))

  def ignore()(implicit pos: SourceLocation): Unit =
    throw new IgnoredException(None, Some(pos))

  def ignore(reason: String)(implicit pos: SourceLocation): Unit =
    throw new IgnoredException(Some(reason), Some(pos))

  def fail()(implicit pos: SourceLocation): Unit =
    throw new AssertionError("failed")

  def fail(reason: String)(implicit pos: SourceLocation): Unit =
    throw new AssertionError(reason)
}
