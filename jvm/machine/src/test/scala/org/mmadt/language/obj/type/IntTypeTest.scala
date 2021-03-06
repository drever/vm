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

import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class IntTypeTest extends FunSuite {
  test("int infix operators"){
    assertResult("bool<=int[plus,2][gt,4]")((int + 2 > 4).toString)
    assertResult("int{?}<=int[plus,2][is,bool<=int[gt,4]]")((int + 2 is int.gt(4)).toString)
  }
  test("int: refinement types"){
    assertResult("int[is,bool<=int[gt,5]]")((int <= int.is(int.gt(5))).toString())
    // TODO: When the stream goes from parallel to serial, quantifiers are not predictable
    /*intercept[IllegalArgumentException]{
      println(int <= int.is(int.gt(5)))
      println(int(5) ==> (int <= int.is(int.gt(5))))
    }*/
    //assertResult("5{0}")((int(5) ==> int.is(int.gt(5))).toString)
    intercept[AssertionError]{
      println(int.q(0) <= int.is(int.gt(5)))
      println(int(6) ==> int.q(0) <= int.is(int.gt(5)))
    }
  }
  test("int: deep nest"){
    assertResult(int(2))(int(1) ==> int.plus(1))
    assertResult(int(3))(int(1) ==> int.plus(int.plus(1)))
    assertResult(int(4))(int(1) ==> int.plus(int.plus(int.plus(1))))
    assertResult(int(5))(int(1) ==> int.plus(int.plus(int.plus(int.plus(1)))))
    assertResult(int(6))(int(1) ==> int.plus(int.plus(int.plus(int.plus(int.plus(1))))))
  }

  test("int: type structure"){
    println(int.plus(int(2)).mult(int(5)).insts)
  }

  test("int: pattern matching"){
    assert(int.test(int))
    assert(!int.test(str))
  }
}