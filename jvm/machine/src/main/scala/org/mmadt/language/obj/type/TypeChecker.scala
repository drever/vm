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

package org.mmadt.language.obj.`type`

import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{RecValue, Value}

import scala.collection.mutable

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object TypeChecker {
  def matchesVT[O <: Obj](obj:Value[O],pattern:Type[O]):Boolean ={
    (pattern.name.equals(Tokens.obj) || // all objects are obj
     (!obj.name.equals(Tokens.rec) && (obj.name.equals(pattern.name) || pattern.domain().name.equals(obj.name))) || // nominal type checking (prevent infinite recursion on recursive types) w/ structural on atomics
     obj.isInstanceOf[Strm[Obj]] || // TODO: testing a stream requires accessing its values (we need strm type descriptors associated with the strm -- or strms are only checked nominally)
     (obj.isInstanceOf[RecValue[_,_]] &&
      testRecord(obj.value.asInstanceOf[Map[Obj,Obj]],pattern.asInstanceOf[ORecType].value()))) && // structural type checking on records
    withinQ(obj,pattern) // must be within the type's quantified window
  }

  def matchesVV[O <: Obj](obj:Value[O],pattern:Value[O]):Boolean =
    obj.value.equals(pattern.value) &&
    withinQ(obj,pattern)

  def matchesTT[O <: Obj](obj:Type[O],pattern:Type[O]):Boolean ={
    ((obj.name.equals(Tokens.obj) || pattern.name.equals(Tokens.obj)) || // all objects are obj
     (!obj.name.equals(Tokens.rec) && obj.name.equals(pattern.name)) ||
     (obj match {
       case recType:ORecType if pattern.isInstanceOf[RecType[_,_]] => testRecord(recType.value(),pattern.asInstanceOf[ORecType].value())
       case _ => false
     })) &&
    obj.insts
      .map(_._2)
      .zip(pattern.insts.map(_._2))
      .map(insts => insts._1.op().equals(insts._2.op()) &&
                    insts._1.args().zip(insts._2.args()).
                      map(a => a._1.test(a._2)).
                      fold(insts._1.args().length == insts._2.args().length)(_ && _))
      .fold(obj.insts.length == pattern.insts.length)(_ && _) &&
    withinQ(obj,pattern)
  }

  def matchesTV[O <: Obj](obj:Type[O],pattern:Value[O]):Boolean = false

  ////////////////////////////////////////////////////////

  def typeCheck[S <: Obj](obj:S,checkType:Type[S]):Unit ={
    assert(obj match {
      case atype:Type[S] => atype.range.test(checkType)
      case avalue:Value[S] => avalue.test(checkType)
    },obj + " is not in " + checkType)
  }

  private def testRecord(leftMap:Map[Obj,Obj],rightMap:Map[Obj,Obj]):Boolean ={
    if (leftMap.equals(rightMap)) return true
    val typeMap:mutable.Map[Obj,Obj] = mutable.Map() ++ rightMap
    leftMap.map(a => typeMap.find(k =>
      a._1.test(Type.resolve(a._1,k._1)) &&
      a._2.test(Type.resolve(a._2,k._2))).map(z => typeMap.remove(z._1))).toList
    typeMap.isEmpty || !typeMap.values.exists(x => x.q._1.value != 0)
  }
}
