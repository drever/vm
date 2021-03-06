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

import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Obj, Str}
import org.mmadt.language.{LanguageFactory, Tokens}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite


/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmlangScriptEngineTest extends FunSuite {

  lazy val engine:mmADTScriptEngine = LanguageFactory.getLanguage("mmlang").getEngine.get()

  test("empty space parsing"){

    assert(!engine.eval("").alive())
    assert(!engine.eval("    ").alive())
    assert(!engine.eval("  \n  ").alive())
    assert(!engine.eval("\t  \n  ").alive())
  }

  test("canonical type parsing"){
    assertResult(bool)(engine.eval("bool").next)
    assertResult(int)(engine.eval("int").next)
    assertResult(str)(engine.eval("str").next)
    assertResult(rec)(engine.eval("rec[]").next)
    assertResult(rec)(engine.eval("rec").next)
  }

  test("quantified canonical type parsing"){
    assertResult(bool.q(int(2)))(engine.eval("bool{2}").next)
    assertResult(int.q(int(0),int(1)))(engine.eval("int{?}").next)
    assertResult(str)(engine.eval("str{1}").next)
    assertResult(rec.q(int(5),int(10)))(engine.eval("rec[]{5,10}").next)
    assertResult(rec.q(int(5),int(10)))(engine.eval("rec{5,10}").next)
  }

  test("atomic value parsing"){
    assertResult(btrue)(engine.eval("true").next)
    assertResult(bfalse)(engine.eval("false").next)
    assertResult(int(5))(engine.eval("5").next)
    assertResult(int(-51))(engine.eval("-51").next)
    assertResult(str("marko"))(engine.eval("'marko'").next)
    assertResult(str("marko comp3 45AHA\"\"\\'-%^&"))(engine.eval("'marko comp3 45AHA\"\"\\'-%^&'").next)
  }

  test("atomic named value parsing"){
    assertResult(vbool(name = "keep",value = true))(engine.eval("keep:true").next)
    assertResult(vint(name = "nat",value = 5))(engine.eval("nat:5").next)
    assertResult(vint(name = "score",value = -51))(engine.eval("score:-51").next)
    assertResult(vstr(name = "fname",value = "marko"))(engine.eval("fname:'marko'").next)
    assertResult(vstr(name = "garbage",value = "marko comp3 45AHA\"\"\\'-%^&"))(engine.eval("garbage:'marko comp3 45AHA\"\"\\'-%^&'").next)
  }

  test("rec value parsing"){
    assertResult(rec(str("name") -> str("marko")))(engine.eval("['name'->'marko']").next)
    assertResult(rec(str("name") -> str("marko"),str("age") -> int(29)))(engine.eval("['name'->'marko','age'->29]").next)
    assertResult(rec(str("name") -> str("marko"),str("age") -> int(29)))(engine.eval("['name'->  'marko' , 'age' ->29]").next)
  }

  test("rec type parsing"){
    assertResult(trec(str("name") -> str,str("age") -> int))(engine.eval("rec[   'name'   ->str ,   'age' ->int]").next)
    assertResult(trec(str("name") -> str,str("age") -> int))(engine.eval("rec['name'->str,'age'->int]").next)
    assertResult(trec(str("name") -> str,str("age") -> int).q(30))(engine.eval("rec['name'->str,'age'->int]{30}").next)
    assertResult(bool.q(30) <= trec(str("name") -> str,str("age") -> int).q(30).get("age",int).gt(30))(engine.eval("rec['name'->str,'age'->int]{30}[get,'age'][gt,30]").next)
    assertResult(bool.q(30) <= trec(str("name") -> str,str("age") -> int).q(30).get("age",int).gt(30))(engine.eval("bool{30}<=rec['name'->str,'age'->int]{30}[get,'age'][gt,30]").next)
    assertResult(bool.q(*) <= trec(str("name") -> str,str("age") -> int).q(*).get("age",int).gt(30))(engine.eval("bool{*}<=rec['name'->str,'age'->int]{*}[get,'age'][gt,30]").next)
  }

  test("rec named value parsing"){
    assertResult(vrec(name = "person",Map(str("name") -> str("marko"),str("age") -> int(29))))(engine.eval("person:[   'name'   ->'marko' ,   'age' ->29]").next)
    assertResult(vrec(name = "person",Map(str("name") -> str("marko"),str("age") -> int(29))))(engine.eval("person:['name'->'marko','age'->29]").next)
  }

  test("composite type get/put"){
    val person:RecType[StrValue,ObjType] = trec(str("name") -> str,str("age") -> int)
    assertResult(str <= person.get(str("name")))(engine.eval("str<=rec['name'->str,'age'->int][get,'name']").next)
    assertResult(int <= person.get(str("age")))(engine.eval("int<=rec['name'->str,'age'->int][get,'age']").next)
    assertResult(int <= rec.put(str("age"),int).get(str("age")))(engine.eval("rec[][put,'age',int][get,'age']").next)
    assertResult(int <= rec.put(str("age"),int).get(str("age")).plus(int(10)))(engine.eval("rec[][put,'age',int][get,'age'][plus,10]").next)
    assertResult(int <= rec.put(str("age"),int).get(str("age")).plus(int(10)))(engine.eval("int<=rec[][put,'age',int][get,'age'][plus,10]").next)
    assertResult(int(20))(engine.eval("['name'->'marko'] rec[][put,'age',10][get,'age'][plus,10]").next)
    assertResult(int(20))(engine.eval("['name'->'marko'] int<=rec[][put,'age',10][get,'age'][plus,10]").next)
  }

  test("quantified value parsing"){
    assertResult(btrue.q(int(2)))(engine.eval("true{2}").next)
    assertResult(bfalse)(engine.eval("false{1}").next)
    assertResult(int(5).q(+))(engine.eval("5{+}").next)
    assertResult(int(6).q(qZero))(engine.eval("6{0}").next)
    assertResult(int(7).q(qZero))(engine.eval("7{0,0}").next)
    assertResult(str("marko").q(int(10),int(100)))(engine.eval("'marko'{10,100}").next)
    assertResult(int(20).q(int(10)))(engine.eval("13{10}[plus,7]").next())
    assertResult(int(13).q(int(10)))(engine.eval("13{10}[is>5]").next())
  }

  test("anonymous type"){
    assertResult(__.plus(1).mult(2))(engine.eval("[plus,1][mult,2]").next())
    assertResult(__.plus(1).mult(__.plus(10)))(engine.eval("[plus,1][mult,[plus,10]]").next())
    assertResult(int(75))(engine.eval("4[plus,1][mult,[plus,10]]").next())
  }

  test("quantifier inst parsing"){
    // assertResult(true)(engine.eval("[plus,2]{2}[mult,3]{32}[plus,4]").next()) // TODO: support instruction quantification (requires a full refactor of the inst obj model)
  }

  test("refinement type parsing"){
    assertResult(int.q(?) <= int.is(int.gt(int(10))))(engine.eval("int[is,int[gt,10]]").next)
    //    assertResult(int <= int.is(int.gt(int(10))))(engine.eval("int<=int[is,int[gt,10]]").next) //TODO: when a range is specified by the user, use that during compilation
    assertResult(int.q(?) <= int.is(int.gt(int(10))))(engine.eval("int[is>10]").next)
  }

  test("as instruction parsing"){
    assertResult(int(1))(engine.eval("1[as,int]").next)
    assertResult(str("1"))(engine.eval("1[as,str]").next)
    assertResult(int(14))(engine.eval("'1'[plus,'4'][as,int]").next)
    assertResult(int(16))(engine.eval("'1'[plus,'4'][as,int[plus,2]]").next)
    assertResult(int(16))(engine.eval("'1'[plus,'4'][as,int][plus,2]").next)
    assertResult(str("14"))(engine.eval("5[plus,2][mult,2][as,str]").next)
    assertResult(str("14hello"))(engine.eval("5 int[plus,2][mult,2]str[plus,'hello']").next)
    assertResult(str("14hello"))(engine.eval("5[plus,2][mult,2]str[plus,'hello']").next)
    assertResult(vrec(str("x") -> int(7)))(engine.eval("5 int[plus,2][as,rec['x'->int]]").next)
    assertResult(vrec(str("x") -> int(7),str("y") -> int(10)))(engine.eval("5 int[plus 2]<x>[plus 3]<y>[as,rec['x'-><x>,'y'-><y>]]").next)
    assertResult(vrec(str("x") -> int(7),str("y") -> int(10)))(engine.eval("5 int[plus 2]<x>[plus 3]<y>[as,rec['x'->int<x>,'y'->int<y>]]").next)
    assertResult(vrec(str("x") -> int(7),str("y") -> int(10),str("z") -> vrec(str("a") -> int(17))))(engine.eval("5 int[plus 2]<x>[plus 3]<y>[as,rec['x'->int<x>,'y'->int<y>,'z'->[as,rec['a'-><x> + <y>]]]]").next)
  }

  test("a instruction parsing"){
    assertResult(btrue)(engine.eval("1[a,int]").next())
    assertResult(bfalse)(engine.eval("'1'[a,int]").next())
    assertResult(int(1))(engine.eval("1[is?int]").next())
    assertResult(int(1))(engine.eval("1 is?int").next())
    assertResult(int(1))(engine.eval("1is?int").next())
  }

  test("endomorphic type parsing"){
    assertResult(int.plus(int.mult(int(6))))(engine.eval("int[plus,int[mult,6]]").next)
  }
  test("explain instruction parsing"){
    assert(engine.eval("int[plus,int[mult,6]][explain]").next().toString.contains("instruction"))
    assert(engine.eval("int[plus,[plus,2][mult,7]]<x>[mult,[plus,5]<y>[mult,[plus,<y>]]][is,[gt,<x>]<z>[id]][plus,5][explain]").next().toString.contains("bool<z>"))
  }

  test("choose instruction parsing"){
    List(
      int.plus(int(2)).choose[IntType,ObjType](int.is(int.gt(int(10))) -> int.gt(int(20)),int -> int.plus(int(10))),
      int.plus(int(2)).choose(trec[IntType,ObjType](int.is(int.gt(int(10))) -> int.gt(int(20)),int -> int.plus(int(10)))),
      int.plus(int(2)).choose(trec[IntType,ObjType](name = Tokens.rec,value = Map[IntType,ObjType](int.is(int.gt(int(10))) -> int.gt(int(20)),int -> int.plus(int(10)))))).
      foreach(chooseInst => {
        assertResult(chooseInst)(engine.eval("int[plus,2][choose,rec[int[is,int[gt,10]]->int[gt,20] | int->int[plus,10]]]").next)
        assertResult(chooseInst)(engine.eval("int[plus,2][int[is,int[gt,10]]->int[gt,20] | int->int[plus,10]]").next)
        assertResult(chooseInst)(engine.eval("int[plus,2][int[is,[gt,10]]->[gt,20] | int->[plus,10]]").next)
        assertResult(chooseInst)(engine.eval("int[plus,2][int[is,[gt,10]]->int[gt,20] | int->int[plus,10]]").next)
        assertResult(chooseInst)(engine.eval("int[plus,2][[is,[gt,10]]->[gt,20] | int->[plus,10]]").next)
        assertResult(chooseInst)(engine.eval(
          """
            | int[plus,2]
            |    [int[is,int[gt,10]] -> int[gt,20]
            |    |int                -> int[plus,10]]""".stripMargin).next)
      })
  }

  test("choose with mixed end types"){
    assertResult(btrue)(engine.eval("  5 [plus,2][[is>5]->true|[is==1]->[plus 2]|int->20]").next)
    assertResult(int(3))(engine.eval("-1 [plus,2][[is>5]->true|[is==1]->[plus2]|int->20]").next)
    assertResult(int(20))(engine.eval("1 [plus,2][[is>5]->true|[is==1]->[plus 2]|int->20]").next)
    assertResult(obj)(engine.eval("int[plus,2][int[is>5]->true|[is==1]->[plus2]|int->20]").next.asInstanceOf[Type[Obj]].range)
    //
    assertResult(btrue.q(int(3)))(engine.eval("             5{3} [plus,2][[is>5]->true|[is==1]->[plus,2]|int->20]").next)
    assertResult(int(3).q(int(5)))(engine.eval("           -1{5} [plus,2][[is>5]->true|[is==1]->[plus,2]|int->20]").next)
    assertResult(int(20).q(int(8),int(10)))(engine.eval("1{8,10} [plus,2][[is>5]->true|[is==1]->[plus,2]|int->20]").next)
    assertResult(obj.q(+))(engine.eval("int{+}[plus,2][[is>5]->true|[is==1]->[plus,2]|int->20]").next.asInstanceOf[Type[Obj]].range)
  }

  test("traverser read/write state parsing"){
    assertResult(int.to("a").plus(int(10)).to("b").mult(int(20)))(engine.eval("int<a>[plus,10]<b>[mult,20]").next)
    assertResult(int.to("a").plus(int(10)).to("b").mult(int.from[IntType]("a")))(engine.eval("int<a>[plus,10]<b>[mult,<a>]").next)
    assertResult(int.to("a").plus(int(10)).to("b").mult(int.from[IntType]("a")))(engine.eval("int<a>[plus,10]<b>[mult,int<a>]").next)
    assertResult(int.to("a").plus(int(10)).to("b").mult(int.from[IntType]("a")))(engine.eval("int<a>[plus,10]int<b>[mult,int<a>]").next)
    assertResult(int(21))(engine.eval("5[plus,2]<x>[mult,2][plus,<x>]").next)
    assertResult(int(21))(engine.eval("5[plus,2]<x>[mult,2][plus,int<x>]").next)
    assertResult("int[plus,2]<x>[mult,2]<y>[plus,int<x>[plus,int<y>]]")(engine.eval("int[plus,2]<x>[mult,2]<y>[plus,<x>[plus,<y>]]").next.toString)
    assertResult(int(35))(engine.eval("5[plus,2]<x>[mult,2]<y>[plus,int<x>[plus,<y>]]").next)
  }

  test("infix operator instruction parsing"){
    assertResult(int.plus(int(6)))(engine.eval("int+6").next)
    assertResult(int.plus(int(6)).gt(int(10)))(engine.eval("int+6>10").next)
    assertResult(int.plus(int(6)).lt(int(10)))(engine.eval("int+6<10").next)
    assertResult(int.plus(int(6)).lte(int(10)))(engine.eval("int+6=<10").next)
    assertResult(int.plus(int(6)).gte(int(10)))(engine.eval("int+6>=10").next)
    assertResult(int.plus(int(1)).mult(int(2)).gt(int(10)))(engine.eval("int+1*2>10").next)
    assertResult(str.plus(str("hello")))(engine.eval("str+'hello'").next)
    assertResult(int.is(int.gt(int(5))))(engine.eval("int[is,int[gt,5]]").next())
    assertResult(int.is(int.gt(int(5))))(engine.eval("int[is>5]").next)
    assertResult(int.is(int.gt(int(5))))(engine.eval("int[is > 5]").next)
    assertResult(int.is(int.lt(int(5))))(engine.eval("int[is < 5]").next)
    assertResult(int.is(int.lte(int(5))))(engine.eval("int[is =< 5]").next)
    assertResult(int.is(int.gte(int(5))))(engine.eval("int[is >= 5]").next)
  }

  test("get dot-notation parsing"){
    assertResult(__.get(str("a")).get(str("b")).get(str("c")))(engine.eval(".a.b.c").next)
    assertResult(int(4))(engine.eval(
      """
        |['a':
        |  ['aa':1,
        |   'ab':2],
        | 'b':
        |   ['ba':3,
        |    'bb':
        |      ['bba':4]]].b.bb.bba""".stripMargin).next())
    assertResult(int(0))(engine.eval("['a':['b':['c':['d':0]]]].a.b.c.d").next)
  }

  test("bool strm input parsing"){
    assertResult(Set(btrue))(engine.eval("true,false bool{*}[is,[id]]").toSet)
    assertResult(Set(btrue))(engine.eval("true,false[is,[id]]").toSet)
  }

  test("int strm input parsing"){
    assertResult(Set(int(-1),int(0)))(engine.eval("0,1 int{+}[plus,-1]").toSet)
    assertResult(Set(int(1),int(2),int(3)))(engine.eval("0,1,2[plus,1]").toSet)
    assertResult(Set(int(30),int(40)))(engine.eval("0,1,2,3 int{2,5}[plus,1][is,int[gt,2]][mult,10]").toSet)
    assertResult(Set(int(300),int(40)))(engine.eval("0,1,2,3[plus,1][is,int[gt,2]][int[is,int[gt,3]] -> int[mult,10] | int -> int[mult,100]]").toSet)
    // assertResult(Set(int(30),int(40)))(engine.eval("0,1,2,3 ==> (int{3}=>int[plus,1][is,int[gt,2]][mult,10])")).toSet)
  }

  test("str strm input parsing"){
    assertResult(str("marko"))(engine.eval("""'m','a','r','k','o' str{*}[fold,'seed','',str[plus,str<seed>]]""").next)
    assertResult(int(5))(engine.eval("""'m','a','r','k','o'[count]""").next)
  }

  test("rec strm input parsing"){
    assertResult(Set(vrec(str("a") -> int(1),str("b") -> int(0)),vrec(str("a") -> int(2),str("b") -> int(0))))(engine.eval("""['a'->1],['a'->2][plus,['b'->0]]""").toSet)
  }

  test("anonymous expression parsing"){
    assertResult(int.is(int.gt(int.id())))(engine.eval("int[is,[gt,[id]]]").next)
    assertResult(int.plus(int(1)).plus(int.plus(int(5))))(engine.eval("int[plus,1][plus,[plus,5]]").next)
    assertResult(int.plus(int(1)).is(int.gt(int(5))))(engine.eval("int[plus,1][is,[gt,5]]").next)
    assertResult(int.q(?) <= int.is(int.gt(int(5))))(engine.eval("int[is,[gt,5]]").next)
    assertResult(int.q(?) <= int.is(int.gt(int.mult(int.plus(int(5))))))(engine.eval("int[is,[gt,[mult,[plus,5]]]]").next)
    assertResult(int.q(?) <= int.is(int.gt(int.mult(int.plus(int(5))))))(engine.eval("int[is,int[gt,int[mult,int[plus,5]]]]").next)
    assertResult(engine.eval("int[is,int[gt,int[mult,int[plus,5]]]]").next)(engine.eval("int[is,[gt,[mult,[plus,5]]]]").next)
    assertResult(int.choose(int.is(int.gt(int(5))) -> int(1),int -> int(2)))(engine.eval(" int[[is>5] -> 1 | int -> 2]").next)
    assertResult(int.plus(int(10)).choose(trec[Obj,Obj](int.is(int.gt(int(10))) -> int.gt(int(20)),int -> int.plus(int(10)))))(engine.eval(" int[plus,10][[is,[gt,10]]->[gt,20] | int->[plus,10]]").next)
    assertResult(int.plus(int(10)).choose(int.is(int.gt(int(5))) -> int(1),int -> int(2)))(engine.eval(" int[plus,10][[is>5] -> 1 | int -> 2]").next)
    assertResult(Set(int(302),int(42)))(engine.eval(
      """ 0,1,2,3
        | [plus,1][is>2]
        |   [ is>3 -> [mult,10]
        |   | int  -> [mult,100]][plus,2]""".stripMargin).toSet)
    assertResult(bfalse)(engine.eval("4[plus,1][[is>5] -> true | int -> false]").next)
    assertResult(btrue)(engine.eval("5[plus,1][[is>5] -> true | int -> false]").next)
    assertResult(btrue)(engine.eval("true[bool -> bool | int -> int]").next)
    assertResult(int(10))(engine.eval("10[bool -> bool | int -> int]").next)
    assertResult(int(10))(engine.eval("10[bool -> true | int -> int]").next)
    assertResult(int(11))(engine.eval("10[bool -> true | int -> int[plus,1]]").next)
  }

  test("expression parsing"){
    assertResult(btrue)(engine.eval("true bool[is,bool]").next)
    assertResult(int(7))(engine.eval("5 int[plus,2]").next)
    assertResult(int(70))(engine.eval("10 int[plus,int[mult,6]]").next)
    assertResult(int(55))(engine.eval("5 int[plus,int[mult,int[plus,5]]]").next)
    assertResult(bfalse)(engine.eval("0 int+1*2>10").next)
    assertResult(str("marko rodriguez"))(engine.eval("'marko' str[plus,' '][plus,'rodriguez']").next)
    assertResult(int(10))(engine.eval("10 int[is,bool<=int[gt,5]]").next)
    assertResult(int.q(?) <= int.plus(int(10)).is(int.gt(int(5))))(engine.eval(" int[plus,10][is,bool<=int[gt,5]]").next)
    assertResult(int.q(?) <= int.plus(int(10)).is(int.gt(int(5))))(engine.eval("int[plus,10][is,int[gt,5]]").next)
    assertResult(int.q(0,3) <= int.q(3).plus(int(10)).is(int.q(3).gt(int(5))))(engine.eval("int{3}[plus,10][is,int[gt,5]]").next)
  }

  test("reducing expressions"){
    assertResult(int(7))(engine.eval("5{7} int{7}[plus,2][count]").next)
    assertResult(int(7))(engine.eval("5{7} [plus,2][count]").next)
    assertResult(int(5))(engine.eval("1,3,7,2,1 int{3,100}[plus,2][count]").next)
    assertResult(int(6))(engine.eval("1,3,7,2,1,10 [plus,2][count]").next)
    assertResult(int(2))(engine.eval("1,3,7,2,1,10 +2[is>5][count]").next)
    ///
    assertResult(int(7))(engine.eval("1,2,3 int{1,7}[fold,'seed',1,[plus,int<seed>]]").next)
  }

  test("logical expressions"){
    assertResult(btrue)(engine.eval("true[and,true]").next)
    assertResult(btrue.q(3))(engine.eval("true{3}[and,true][or,false]").next)
    assertResult(btrue.q(3))(engine.eval("true{3}[and,true][or,[and,bool]]").next)
    assertResult(bfalse.q(3,30))(engine.eval("true{3,30}[and,false][or,[and,bool]]").next)
  }

  test("composite expression parsing"){
    assertResult(trec(str("age") -> int).id())(engine.eval("rec['age'->int][id]").next())
    assertResult(vrec(str("age") -> int(29)))(engine.eval("['age'->29] rec['age'->int][id]").next())
    assertResult(trec(str("age") -> int,str("name") -> str) <= trec[Str,Obj](str("age") -> int).put(str("name"),str))(engine.eval("rec['age'->int][put,'name',str]").next())
    assertResult(rec(str("age") -> int(29),str("name") -> str("marko")))(engine.eval(
      """
        |['age'->29] rec['age'->int][choose,rec[
        |  rec['age'->int][is,rec['age'->int][get,'age'][gt,30]] -> rec['age'->int][put,'name','bill'] |
        |  rec['age'->int]                                       -> rec['age'->int][put,'name','marko']]]""".stripMargin).next())
    assertResult(rec(str("age") -> int(29),str("name") -> str("marko")))(engine.eval(
      """
        |['age'->29] rec['age'->int][
        |  is.age>30  -> [put,'name','bill'] |
        |  rec        -> [put,'name','marko']]""".stripMargin).next())
  }

  /*test("model parsing"){
    val engine2                          = LanguageFactory.getLanguage("mmlang").getEngine.get()
    val person:RecType[StrValue,ObjType] = trec(str("name") -> str,str("age") -> int)
    // model creation
    assertResult(trec(tobj("nat") -> (int <= int.is(int.gt(0)))))(engine2.eval("rec[nat -> int<=int[is>0]]").next)
    assertResult(trec[ObjType,ObjType](
      tobj("nat") -> (int <= int.is(int.gt(0))),
      tobj("person") -> trec(
        str("name") -> str,
        str("age") -> tobj("nat"))))(engine2.eval("rec[nat -> int<=int[is>0] | person -> rec['name'->str,'age'->nat]]").next)
    val model:Model = Model.from(engine2.eval("rec[nat -> int<=int[is>0] | person -> rec['name'->str,'age'->nat]]").next.asInstanceOf[RecType[Type[Obj],Type[Obj]]])
    engine2.put("model",model)
    assertResult(model)(engine2.get("model"))
    // model compilations
    assertResult(int <= int.is(int.gt(0)))(engine2.eval("nat").next)
    assertResult(int(4))(engine2.eval("2 nat[plus,2]").next)
    assertResult(int <= int.is(int.gt(0)).plus(1))(engine2.eval("nat+1").next)
    assertResult(trec(value = Map(str("name") -> str,str("age") -> tobj("nat"))))(engine2.eval("person").next)
    assertResult(str <= person.get(str("name"),str))(engine2.eval("person[get,'name']").next)
    assertResult(person.get(str("age"),int.named("nat")).plus(1))(engine2.eval("person[get,'age'][plus,1]").next)

    // model type checking
    /*  assertThrows[AssertionError]{ // TODO: requires no type erasure and model checking
    engine.eval("-2 nat[plus,2]").next()
     }*/
    /*assertThrows[AssertionError]{ // TODO: requires that a specified range can't be overridden by abstract interpretation
      println(engine.eval("nat<=nat[plus,-3]").next())
      engine.eval("2 nat<=nat[plus,-3]").next()
    }*/
    //assertResult(int(30))(engine.eval("['name'->'marko','age'->29]person[get,'age',nat][plus,1]").next) // TODO: need to type check the values against the domain of the type (we currently only type check types against types)
    //assertResult(false)(engine.eval("['name'->'ryan','age'->10],['name'->'marko','age'->29] person[get,'age'][plus,1]").next) // TODO: need to type check the values against the domain of the type (we currently only type check types against types)
  }*/
}