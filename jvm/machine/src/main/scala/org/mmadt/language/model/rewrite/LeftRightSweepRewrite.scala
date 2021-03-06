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

import org.mmadt.language.Tokens
import org.mmadt.language.model.Model
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.OpInstResolver
import org.mmadt.language.obj.value.Value
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object LeftRightSweepRewrite {

  def rewrite[S <: Obj](model:Model,atype:Type[S],btype:Type[S],traverser:Traverser[S]):Traverser[S] ={
    if (atype.insts.nonEmpty) {
      model.get(atype) match {
        case Some(right:Type[S]) => rewrite(model,right,btype,traverser)
        case None =>
          val inst:Inst[Obj,Obj] = OpInstResolver.resolve(atype.insts.last._2.op(),rewriteArgs(model,atype.rinvert[Type[S]]().range,atype.insts.last._2,traverser))
          rewrite(model,
            atype.rinvert(),
            inst.apply(traverser.split(atype.rinvert[Type[S]]().range)).obj().asInstanceOf[Type[S]].compose(btype), // might need a model.resolve down the road
            traverser)
      }
    } else if (btype.insts.nonEmpty) {
      rewrite(model,
        btype.linvert(),
        btype.linvert().domain(),
        btype.insts.head._2.apply(traverser)).asInstanceOf[Traverser[S]]
    }
    else traverser
  }

  // if no match, then apply the instruction after rewriting its arguments
  private def rewriteArgs[S <: Obj](model:Model,start:Type[S],inst:Inst[Obj,Obj],traverser:Traverser[S]):List[Obj] ={
    inst.op() match {
      case Tokens.a | Tokens.as | Tokens.map | Tokens.put => inst.args()
      case Tokens.choose =>
        def branching(obj:Obj):Obj ={
          obj match {
            case branchType:Type[S] => rewrite(model,branchType,start,traverser.split(start)).obj()
            case branchValue:Value[_] => branchValue
          }
        }
        List(trec(name = Tokens.rec,inst.arg0[ORecType]().value().map(x => (branching(x._1),branching(x._2)))))
      case _ => inst.args().map{
        case atype:Type[_] => rewrite(model,atype,start,traverser.split(start)).obj()
        case avalue:Value[_] => avalue
      }
    }
  }


}