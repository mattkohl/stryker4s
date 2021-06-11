package stryker4s.mutants

import cats.data.NonEmptyChain
import stryker4s.model.{Mutable, PlaceableTree}

import scala.meta.Tree

package object tree {
  type Mutables = NonEmptyChain[Mutable]

  type MutantMatcher = PartialFunction[(Tree, PlaceableTree), Mutables]
}
