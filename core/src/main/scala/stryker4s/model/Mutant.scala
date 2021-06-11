package stryker4s.model

import mutationtesting.Location
import stryker4s.extension.mutationtype.Mutation

import scala.meta.{Term, Tree}

final case class Mutant(id: Int, original: Term, mutated: Term, mutationType: Mutation[_ <: Tree])

final case class Mutable(mutatedStatement: Tree, metadata: MutableMetadata)

/** Metadata of a [[stryker4s.model.Mutable]], not to be confused with metadata that is mutable
  */
final case class MutableMetadata(original: String, replacement: String, location: Location)
