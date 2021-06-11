package stryker4s.mutants.tree

import cats.data.NonEmptyChain
import mutationtesting.{Location, Position}
import stryker4s.model.Mutable
import stryker4s.testutil.Stryker4sSuite

import java.util.concurrent.atomic.AtomicInteger
import scala.meta._
import stryker4s.model.MutableMetadata

class MutantCollectorTest extends Stryker4sSuite {

  describe("onEnter") {
    it("should only call 'isDefinedAt' on the PartialFunction once") {
      val counter = new AtomicInteger(0)
      val pf: MutantMatcher = {
        // if-guard is always true and counts how many times it is called
        case (Term.Name(">"), topStatement) if counter.incrementAndGet() != -1 =>
          NonEmptyChain.one(
            Mutable(topStatement.tree, MutableMetadata("<", ">", Location(Position(0, 7), Position(0, 8))))
          )
      }
      val sut = new MutantCollector(pf, _ => true)

      sut(q"def bar = 15 > 14")

      counter.get() shouldBe 1
    }
  }
}
