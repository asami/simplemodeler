package org.simplemodeling.dsl

import org.goldenport.sdoc.SDoc

/*
 * Dec.  5, 2008
 * Dec.  6, 2008
 */
class SExecutionStep(val kind: ExecutionKind, val entity: SEntity) extends SStep {
  def this(aKind: ExecutionKind) = this(aKind, NullEntity)
}

abstract class ExecutionKind
class Issue extends ExecutionKind
object Issue extends Issue

class Open extends ExecutionKind
object Open extends Open

class Close extends ExecutionKind
object Close extends Close

class Create extends ExecutionKind
object Create extends Create

class Read extends ExecutionKind
object Read extends Read

class Update extends ExecutionKind
object Update extends Update

class Delete extends ExecutionKind
object Delete extends Delete
