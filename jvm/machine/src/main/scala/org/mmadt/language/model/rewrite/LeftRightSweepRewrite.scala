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

package org.mmadt.language.model.rewrite

import org.mmadt.language.model.Model
import org.mmadt.language.obj._
import org.mmadt.processor.Traverser

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object LeftRightSweepRewrite {

  @scala.annotation.tailrec
  def rewrite[E <: Obj](model:Model,atype:OType,btype:OType,traverser:Traverser[E]):Traverser[E] ={
    if (atype.insts().nonEmpty) {
      model.get(atype) match {
        case Some(right:OType) => rewrite(model,right,btype,traverser)
        case None => rewrite(model,
          atype.rinvert(),
          atype.insts().last._2.apply(
            atype.rinvert[OType]().range(),
            rewriteArgs(model,atype.rinvert[OType]().range(),atype.insts().last._2,traverser)).asInstanceOf[OType].compose(btype),
          traverser)
      }
    } else if (btype.insts().nonEmpty) {
      rewrite(model,
        btype.linvert(),
        btype.linvert().domain(),
        traverser.apply(btype.insts().head._2.apply(btype.insts().head._1).asInstanceOf[OType]).asInstanceOf[Traverser[E]])
    }
    else traverser
  }

  // if no match, then apply the instruction after rewriting its arguments
  private def rewriteArgs[E <: Obj](model:Model,start:OType,inst:Inst,traverser:Traverser[E]):List[Obj] ={
    inst.args().map{
      case atype:OType => rewrite(model,atype,start,traverser.split(start)).obj()
      case avalue:O => avalue
    }
  }
}