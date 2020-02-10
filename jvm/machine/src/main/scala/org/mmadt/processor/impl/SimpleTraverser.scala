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

package org.mmadt.processor.impl

import org.mmadt.language.Tokens
import org.mmadt.machine.obj.theory.obj.`type`.Type
import org.mmadt.machine.obj.theory.obj.value.{StrValue, Value}
import org.mmadt.machine.obj.theory.obj.{Inst, Obj}
import org.mmadt.processor.Traverser

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class SimpleTraverser[S <: Obj](obj: S, state: Map[StrValue, Obj]) extends Traverser[S] {

  def this(obj: S) = this(obj, Map()) //
  override def obj(): S = obj //
  override def state(): Map[StrValue, Obj] = state //
  override protected def to(label: StrValue): Traverser[S] = new SimpleTraverser[S](this.obj, Map[StrValue, Obj](label -> this.obj) ++ this.state) //
  override protected def from[E <: Obj](label: StrValue): Traverser[E] = new SimpleTraverser[E](this.state(label).asInstanceOf[E], this.state) //
  override def split[E <: Obj](obj: E): Traverser[E] = new SimpleTraverser[E](obj, this.state)

  override def apply[E <: Obj](t: E with Type[_]): Traverser[E] = {
    if (t.insts().isEmpty)
      this.asInstanceOf[Traverser[E]]
    else {
      (t.insts().head._2 match {
        // traverser instructions
        case toInst: Inst if toInst.op().equals(Tokens.to) => to(toInst.arg())
        case fromInst: Inst if fromInst.op().equals(Tokens.from) => from(fromInst.arg())
        // branch instructions
        // storage instructions
        case storeInst: Inst => this.split(storeInst.inst(storeInst.op(), storeInst.args().map {
          case typeArg: Type[_] => this.apply(typeArg).obj()
          case valueArg: Value[_] => valueArg
        }).apply(this.obj))
      }).asInstanceOf[Traverser[E]]
    }
  }
}
