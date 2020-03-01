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

package org.mmadt.language.mmlang

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.{RecType, Type}
import org.mmadt.language.obj.value.{RecValue, StrValue, Value}
import org.mmadt.language.obj.{Inst, IntQ, Obj}
import org.mmadt.processor.Traverser
import org.mmadt.storage.obj.{int, _}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object mmlangPrinter {

  def qString(x:IntQ):String = x match {
    case `qOne` => Tokens.empty
    case `qZero` => QZERO
    case `qMark` => s"${LCURL}${Tokens.q_mark}${RCURL}"
    case `qPlus` => s"${LCURL}${Tokens.q_plus}${RCURL}"
    case `qStar` => s"${LCURL}${Tokens.q_star}${RCURL}"
    case (x,y) if x == y => s"${LCURL}${x}${RCURL}"
    case (x,y) if y == int(Long.MaxValue) => "{" + x + ",}"
    case (x,y) if x == int(Long.MinValue) => "{," + y + "}"
    case _ => "{" + x._1.value() + "," + x._2.value() + "}"
  }

  def traverserString(trav:Traverser[_]):String ={
    "[" + trav.obj() + "|" + trav.state.foldLeft(EMPTY)((string,x) => string + x._1.toString.replace("'","") + "->" + x._2 + ",").dropRight(1) + "]"
  }

  def typeString(t:Type[Obj]):String ={
    val named = Tokens.named(t.name)
    val range  = (t match {
      case r:RecType[_,_] => (if (named) t.name else "") + (if (r.value().isEmpty) "" else r.value().foldLeft("[")((string,r) => string + r._1 + "->" + r._2 + ",").dropRight(1) + "]")
      case _ => t.name
    }) + qString(t.q())
    val domain = if (t.insts().isEmpty) "" else
      t.insts().head._1.name + qString(t.insts().head._1.q())
    (if (domain.equals("") || range.equals(domain)) range else range + "<=" + domain) +
    t.insts().map(_._2.toString()).fold("")((a,b) => a + b)
  }

  def valueString(v:Value[Obj]):String ={
    val named = Tokens.named(v.name)
    (if (named) v.name else "") + (
      v match {
        case x:RecValue[_,_] => (if (x.value().isEmpty) "" else x.value().foldLeft("[")((string,x) => string + x._1 + ":" + x._2 + ",").dropRight(1) + "]") + qString(x.q())
        case x:StrValue => (if (named) "[" else "") + "'" + v.value() + "'" + (if (named) "]" else "") + qString(x.q())
        case _ => (if (named) "[" else "") + v.value() + (if (named) "]" else "") + qString(v.q())
      })
  }

  def instString(inst:Inst):String ={
    inst.op() match {
      case Tokens.to | Tokens.from => "<" + inst.arg0[StrValue]().value() + ">"
      case _ => inst.args() match {
        case Nil => "[" + inst.op() + "]"
        case args:List[StrValue] if inst.op().equals(Tokens.as) => "[" + inst.op() + "," + args.head.value() + "]"
        case args:List[Obj] => "[" + inst.op() + "," + args.map(x => x.toString + ",").fold("")((a,b) => a + b).dropRight(1) + "]"
      }
    }
  }
}
