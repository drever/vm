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

import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.map.{GtOp, PlusOp}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class __Test extends FunSuite {

  test("__ type in raw form"){
    val x = __(List(PlusOp(int(4)),PlusOp(int.plus(int)),GtOp(__(List(PlusOp(int(2)))))))
    assertResult(int.plus(int(4)).plus(int.plus(int)).gt(int.plus(int(2))))(x(int))
  }

  test("__  type fluency"){
    assertResult(List(str("marko!")))((vrec(str("name") -> str("marko")) ===> __.id().get(str("name")).plus(str("!"))).toList)
    assertResult(List(int(12)))((int(5) ===> __.plus(2).plus(5).id()).toList)
    assertResult(List(int(120)))((int(5) ===> __.plus(2).plus(5).id().mult(10)).toList)
  }

  test("__ deep nest"){
    assertResult(int(2))(int(1) ==> __.plus(1))
    assertResult(int(3))(int(1) ==> __.plus(__.plus(1)))
    assertResult(int(4))(int(1) ==> __.plus(__.plus(__.plus(1))))
    assertResult(int(5))(int(1) ==> __.plus(__.plus(__.plus(__.plus(1)))))
    assertResult(int(6))(int(1) ==> __.plus(__.plus(__.plus(__.plus(__.plus(1))))))
  }

  test("__ quantifiers"){
    println(__.id().q(2).plus(4))
    assertResult(int(5))(int(5) ==> __)
    assertResult(int(5))(int(5) ==> __.id())
    assertResult(int(5))(int(5) ==> __.id().q(1))
    //assertResult(int(5).q(2))(int(5) ==> __.id().q(2))

    /*assertResult(int(1).q(10))(int(1) ==> __.q(10))
    assertResult(int(1).q(*))(int(1) ==> __.q(10).id().q(*))
    assertResult(int(1).q(0,10))(int(1) ==> __.q(10).id().q(?))
    assertResult(int(2).q(10))(int(1) ==> __.plus(1).q(10))
    assertResult(int(2).q(20))(int(1).q(2) ==> __.plus(1).q(10))
    assertResult(int(20).q(20))(int(1).q(2) ==> __.plus(1).q(10).mult(10))
    assertResult(int(20).q(200))(int(1).q(2) ==> __.plus(1).q(10).mult(10).q(100))
    assertResult(int(21).q(200))(int(1).q(2) ==> __.plus(1).q(10).mult(10).q(100).plus(1))
    assertResult(int(42).q(200))(int(1).q(2) ==> __.plus(1).q(10).mult(10).q(100).plus(1).plus(__.id().q(1000)))
    assertResult(int(45).q(200))(int(1).q(2) ==> __.plus(1).q(10).mult(10).q(100).plus(1).plus(__.id().q(1000)).plus(3))*/
  }

}

