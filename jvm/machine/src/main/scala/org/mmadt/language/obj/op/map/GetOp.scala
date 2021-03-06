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

package org.mmadt.language.obj.op.map

import org.mmadt.language.Tokens
import org.mmadt.language.obj.{Inst,Obj,Rec,multQ}
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait GetOp[A <: Obj,B <: Obj] {
  this:Rec[A,B] =>
  def get(key:A):B
  def get[BB <: Obj](key:A,btype:BB):BB
}

object GetOp {
  def apply[A <: Obj,B <: Obj](key:A):Inst[Rec[A,B],B] = new GetInst[A,B](key)
  def apply[A <: Obj,B <: Obj](key:A,typeHint:B):Inst[Rec[A,B],B] = new GetInst(key,typeHint)

  class GetInst[A <: Obj,B <: Obj](key:A,typeHint:B = obj.asInstanceOf[B]) extends VInst[Rec[A,B],B]((Tokens.get,List(key))) {
    override def apply(trav:Traverser[Rec[A,B]]):Traverser[B] = trav.split((typeHint.name match {
      case Tokens.obj => trav.model.resolve(trav.obj()).get(key)
      case _ => trav.model.resolve(trav.obj()).get(key,typeHint)
    }).q(multQ(trav.obj().q,this.q)))
  }

}
