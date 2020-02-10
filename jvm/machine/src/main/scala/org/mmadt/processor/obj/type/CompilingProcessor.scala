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

package org.mmadt.processor.obj.`type`

import org.mmadt.language.obj.`type`.{Type, TypeChecker}
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.processor.obj.`type`.util.InstUtil
import org.mmadt.processor.{Processor, Traverser}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class CompilingProcessor[S <: Obj, E <: Obj] extends Processor[S, E] {
  override def apply(startObj: S, endType: E with Type[_]): Iterator[Traverser[E]] = {
    if (startObj.isInstanceOf[Value[_]]) throw new IllegalArgumentException("The compiling processor only accepts types: " + startObj)
    var mutatingType: E with Type[_] = endType
    var mutatingTraverser: Traverser[E] = new C1Traverser[E](startObj.asInstanceOf[E])
    /////
    while (mutatingType.insts() != Nil) {
      TypeChecker.checkType(mutatingTraverser.obj(), mutatingType)
      val inst: Inst = InstUtil.nextInst(mutatingType.insts()).get
      mutatingTraverser = mutatingTraverser.split(mutatingTraverser.obj().inst(inst.op(), inst.args().map {
        case typeArg: Type[_] => mutatingTraverser.split(mutatingTraverser.obj() match {
          case tt: Type[_] => tt.pure()
          case vv: Value[_] => vv
        }).apply(typeArg).obj()
        case valueArg: Value[_] => valueArg
      }).apply(mutatingTraverser.obj()).asInstanceOf[E with Type[_]])
      mutatingType = mutatingType.pop()
    }
    Iterator(mutatingTraverser)
  }
}
