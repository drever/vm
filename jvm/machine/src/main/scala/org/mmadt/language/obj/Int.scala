/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 *  mm-ADT is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU Affero General Public License as published by the
 *  Free Software Foundation, either version 3 of the License, or (at your option)
 *  any later version.
 *
 *  mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 *  License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 *  You can be released from the requirements of the license by purchasing a
 *  commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.language.obj

import org.mmadt.language.obj.op.filter.IsOp
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.op.traverser.ToOp
import org.mmadt.language.obj.value.IntValue
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Int extends Obj
  with EqsOp[Int]
  with PlusOp[Int]
  with MultOp[Int]
  with NegOp
  with GtOp[Int]
  with GteOp[Int]
  with LtOp[Int]
  with LteOp[Int]
  with IsOp[Int]
  with OneOp[Int]
  with ToOp[Int]
  with ZeroOp[Int]

object Int {
  @inline implicit def longToInt(java:Long):IntValue = int(java)
  @inline implicit def intToInt(java:scala.Int):IntValue = int(java.longValue())
}