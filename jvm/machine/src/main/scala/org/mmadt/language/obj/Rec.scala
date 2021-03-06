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
import org.mmadt.language.obj.op.map.{EqsOp, GetOp, PlusOp}
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.op.traverser.ToOp
import org.mmadt.language.obj.value.{RecValue, Value}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Rec[A <: Obj,B <: Obj] extends Obj
  with EqsOp[Rec[A,B]]
  with PlusOp[Rec[A,B]]
  with IsOp[Rec[A,B]]
  with ToOp[Rec[A,B]]
  with GetOp[A,B]
  with PutOp[A,B] {
  def value():Any
}

object Rec {
  implicit def mapToRec[A <: Value[Obj],B <: Value[Obj]](java:Map[A,B]):RecValue[A,B] = vrec[A,B](java)
  implicit def mapToRec[A <: Value[Obj],B <: Value[Obj]](value:(A,B),values:(A,B)*):RecValue[A,B] = vrec(value = value,values = values:_*)
}
