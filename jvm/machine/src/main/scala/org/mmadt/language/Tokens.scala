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

package org.mmadt.language

import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object Tokens {

  val bool = "bool"
  val str = "str"
  val rec = "rec"
  val int = "int"
  val inst = "inst"

  val and = "and"
  val choose = "choose"
  val get = "get"
  val id = "id"
  val is = "is"
  val plus = "plus"
  val map = "map"
  val mult = "mult"
  val neg = "neg"
  val gt = "gt"
  val or = "or"
  val to = "to"
  val from = "from"
  val start = "start"
  val model = "model"

  def named(objType: String): Boolean = !Set(bool, str, rec, int, inst).contains(objType) // TODO: global immutable set

  def symbol(obj: Obj): String = obj match {
    case _: BoolType => "bool"
    case _: IntType => "int"
    case _: StrType => "str"
    case _: RecType[_, _] => "rec"
    case _: Type[_] => "obj"
    case _ => throw new Exception("Error: " + obj)
  }

}
