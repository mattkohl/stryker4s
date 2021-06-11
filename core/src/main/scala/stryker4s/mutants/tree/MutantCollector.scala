package stryker4s.mutants.tree

import cats.data.AndThen
import cats.syntax.apply._
import cats.syntax.functor._
import stryker4s.model.PlaceableTree

import java.util.concurrent.atomic.AtomicReference
import scala.meta.Tree

class MutantCollector(
    onEnter: MutantMatcher,
    canPlace: Tree => Boolean
) {

  def apply(tree: Tree): Map[PlaceableTree, Mutables] = {
    val placeableTree = new AtomicReference(PlaceableTree(tree))

    // PartialFunction that always matches, to check and pass the last topStatement
    val canPlaceF: PartialFunction[Tree, (Tree, PlaceableTree)] = { case t =>
      if (canPlace(t)) placeableTree.set(PlaceableTree(t))
      (t, placeableTree.get())
    }

    // PartialFunction that _sometimes_ matches and returns a tuple of the expression to be placed at and a `NonEmptyList[Mutable]`
    val onEnterF: PartialFunction[(Tree, PlaceableTree), (PlaceableTree, Mutables)] = tupleRightArg(onEnter)

    // Combine the two PF's
    // Some complex stuff with cats.data.AndThen to make sure the PF is only called once
    val collectF: PartialFunction[Tree, (PlaceableTree, Mutables)] = Function.unlift {
      (AndThen(canPlaceF) andThen (onEnterF.lift)).apply(_)
    }

    // Walk through the tree and create a Map of PlaceableTree and Mutables
    tree.collect(collectF).toMap
  }

  /** Tuple the right-arg of a PartialFunction to the result of the PF.
    */
  def tupleRightArg[A, B, C](pf: PartialFunction[(A, B), C]): PartialFunction[(A, B), (B, C)] = {
    Function.unlift {
      pf.lift.map2(_._2) { case (c, b) => c.tupleLeft(b) }
    }
  }
}
