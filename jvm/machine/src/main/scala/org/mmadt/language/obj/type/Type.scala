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

import org.mmadt.language.model.Model
import org.mmadt.language.obj.op.map.{AOp,IdOp,MapOp,QOp}
import org.mmadt.language.obj.op.model.AsOp
import org.mmadt.language.obj.op.reduce.{CountOp,FoldOp}
import org.mmadt.language.obj.op.sideeffect.{AddOp,ErrorOp}
import org.mmadt.language.obj.op.traverser.{ExplainOp,FromOp}
import org.mmadt.language.obj.value.{StrValue,Value}
import org.mmadt.language.obj.{eqQ,_}
import org.mmadt.language.{LanguageFactory,Tokens}
import org.mmadt.processor.{Processor,Traverser}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Type[+T <: Obj] extends Obj
  with AddOp
  with ExplainOp {
  this:T =>

  // type properties
  val insts:List[(Type[Obj],Inst[Obj,Obj])]
  lazy val canonical:this.type = this.range.q(qOne)
  lazy val range    :this.type = (this match {
    case _:BoolType => tbool(this.name,this.q,Nil)
    case _:IntType => tint(this.name,this.q,Nil)
    case _:StrType => tstr(this.name,this.q,Nil)
    case arec:RecType[_,_] => trec(this.name,arec.value(),arec.q,Nil)
    case _:__ => this
    case _:ObjType => tobj(this.name,this.q,Nil)
  }).asInstanceOf[this.type]

  def domain[D <: Obj]():Type[D] = (this.insts match {
    case Nil => this
    case i:List[(Type[Obj],Inst[_,_])] => i.head._1.range
  }).asInstanceOf[Type[D]]

  // type manipulation functions
  def linvert():this.type ={
    ((this.insts.tail match {
      case Nil => this.range
      case i => i.foldLeft[Traverser[Obj]](Traverser.standard(i.head._1.range))((btype,inst) => inst._2.apply(btype)).obj()
    }) match {
      case vv:Value[_] => vv.start()
      case x => x
    }).asInstanceOf[this.type]
  }
  def rinvert[R <: Type[Obj]]():R =
    (this.insts.dropRight(1).lastOption match {
      case Some(prev) => prev._2.apply(Traverser.standard(prev._1)).obj()
      case None => this.insts.head._1
    }).asInstanceOf[R]

  // type specification and compilation
  final def <=[D <: Obj](domainType:Type[D]):this.type = domainType.compose(this).q(this.q).asInstanceOf[this.type]
  override def ==>[R <: Obj](rangeType:Type[R]):R = Processor.compiler()(this,Type.resolveAnonymous(this,rangeType))
  def ==>[R <: Obj](model:Model)(rangeType:Type[R]):R = Processor.compiler(model)(this,Type.resolveAnonymous(this,rangeType))

  // type constructors via stream ring theory // TODO: figure out how to get this into [mult][plus] compositions
  def compose[R <: Type[Obj]](btype:R):R = btype match {
    case anon:__ => anon(this)
    case atype:Type[Obj] => atype.insts.seq.foldLeft[Traverser[Obj]](Traverser.standard(this))((b,a) => a._2(b)).obj().asInstanceOf[R].named(btype.name)
  }
  def compose(inst:Inst[_,_]):this.type = this.compose(this,inst)
  def compose[R <: Obj](nextObj:R,inst:Inst[_,_]):R ={
    val newInsts = if (inst.op().equals(Tokens.noop)) this.insts else this.insts ::: List((this,inst.asInstanceOf[Inst[Obj,Obj]]))
    (nextObj match {
      case _:Bool => tbool(nextObj.name,multQ(this,inst),newInsts)
      case _:Int => tint(nextObj.name,multQ(this,inst),newInsts)
      case _:Str => tstr(nextObj.name,multQ(this,inst),newInsts)
      case arec:Rec[_,_] => trec(arec.name,arec.value().asInstanceOf[Map[Obj,Obj]],multQ(this,inst),newInsts)
      case _:__ => new __(newInsts.asInstanceOf[List[Tuple2[Type[Obj],Inst[Obj,Obj]]]])
      case _ => tobj(nextObj.name,multQ(this,inst),newInsts)
    }).asInstanceOf[R]
  }

  // obj-level operations
  override def a(atype:Type[Obj]):Bool = this.compose(bool,AOp(atype))
  override def add[O <: Obj](obj:O):O = this.compose(asType(obj).asInstanceOf[O],AddOp(obj))
  override def as[O <: Obj](obj:O):O = this.compose(obj,AsOp(obj))
  override def count():IntType = this.compose(int,CountOp())
  override def id():this.type = this.compose(IdOp())
  override def map[O <: Obj](other:O):O = this.compose(asType(other).asInstanceOf[O],MapOp[O](other))
  override def fold[O <: Obj](seed:(String,O))(atype:Type[O]):O = this.compose(asType(seed._2),FoldOp(seed,atype)).asInstanceOf[O]
  override def from[O <: Obj](label:StrValue):O = this.compose(FromOp(label)).asInstanceOf[O]
  override def from[O <: Obj](label:StrValue,default:Obj):O = this.compose(FromOp(label,default)).asInstanceOf[O]
  override def quant():IntType = this.compose(tint(),QOp())
  override def error(message:String):this.type = this.compose(ErrorOp(message))

  def named(_name:String):this.type = (this match {
    case _:BoolType => tbool(_name,this.q,this.insts)
    case _:IntType => tint(_name,this.q,this.insts)
    case _:StrType => tstr(_name,this.q,this.insts)
    case arec:RecType[_,_] => trec(_name,arec.value(),arec.q,this.insts)
    case _:__ => this
    case _:ObjType => tobj(_name,this.q,this.insts)
  }).asInstanceOf[this.type]

  // pattern matching methods
  override def test(other:Obj):Boolean = other match {
    case argValue:Value[_] => TypeChecker.matchesTV(this,argValue)
    case argType:Type[_] => TypeChecker.matchesTT(this,argType)
  }

  // standard Java implementations
  override def toString:String = LanguageFactory.printType(this)
  override def hashCode:scala.Int = this.name.hashCode ^ this.q.hashCode() ^ this.insts.hashCode()
  override def equals(other:Any):Boolean = other match {
    case atype:Type[_] => this.name == atype.name && eqQ(this,atype) && atype.insts.map(x => (x._1.name,x._2)) == this.insts.map(x => (x._1.name,x._2))
    case _ => false
  }
}

object Type {
  @scala.annotation.tailrec
  def createInstList(list:List[(Type[Obj],Inst[Obj,Obj])],atype:Type[Obj]):List[(Type[Obj],Inst[Obj,Obj])] ={
    if (atype.insts.isEmpty) list else createInstList(List((atype.range,atype.insts.last._2)) ::: list,atype.insts.last._1)
  }

  def nextInst(atype:Type[_]):Option[Inst[Obj,Obj]] = atype.insts match {
    case Nil => None
    case x => Some(x.head._2)
  }

  def resolveAnonymous[R <: Obj](obj:Obj,rangeType:Type[R]):Type[R] = rangeType match {
    case x:__ => x(obj)
    case x:R => x
  }

  def resolve[R <: Obj](objA:Obj,objB:R):R = objB match {
    case x:__ => x(objA)
    case _ => objB
  }
}
