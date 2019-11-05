/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.machine.console;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TReal;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.util.OperatorHelper;
import org.parboiled.Action;
import org.parboiled.BaseParser;
import org.parboiled.Context;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.support.Var;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
@BuildParseTree
public class SimpleParser extends BaseParser<Object> {

    final Map<Integer, Rule> ARROWS = new HashMap<>() {{
        put(0, Terminal(">"));
        put(1, Terminal("->"));
        put(2, Terminal("-->"));
        put(3, Terminal("--->"));
    }};

    final Rule COLON = Terminal(Tokens.COLON);
    final Rule COMMA = Terminal(Tokens.COMMA);
    final Rule PERIOD = Terminal(Tokens.PERIOD);
    final Rule AND = Terminal(Tokens.AMPERSAND);
    final Rule OR = Terminal(Tokens.BAR);
    final Rule STAR = Terminal(Tokens.ASTERIX);
    final Rule PLUS = Terminal(Tokens.CROSS);
    final Rule QMARK = Terminal(Tokens.QUESTION);
    final Rule SUB = Terminal(Tokens.DASH);
    final Rule MAPSFROM = Terminal(Tokens.MAPSFROM);
    final Rule MAPSTO = Terminal(Tokens.MAPSTO);
    final Rule LBRACKET = Terminal(Tokens.LBRACKET);
    final Rule RBRACKET = Terminal(Tokens.RBRACKET);
    final Rule LCURL = Terminal(Tokens.LCURL);
    final Rule RCURL = Terminal(Tokens.RCURL);
    final Rule TRIPLE_QUOTE = Terminal(Tokens.DQUOTE + Tokens.DQUOTE + Tokens.DQUOTE);
    final Rule DOUBLE_QUOTE = Terminal(Tokens.DQUOTE);
    final Rule SINGLE_QUOTE = Terminal(Tokens.SQUOTE);
    final Rule TILDE = Terminal(Tokens.TILDE);
    final Rule LPAREN = Terminal(Tokens.LPAREN);
    final Rule RPAREN = Terminal(Tokens.RPAREN);
    final Rule EQUALS = Terminal(Tokens.EQUALS);
    final Rule GT = Terminal(Tokens.GT);
    final Rule LT = Terminal(Tokens.LT);
    final Rule NEQ = Terminal(Tokens.NEQ);
    final Rule SEMICOLON = Terminal(Tokens.SEMICOLON);
    final Rule TRUE = Terminal(Tokens.TRUE);
    final Rule FALSE = Terminal(Tokens.FALSE);

    final Rule OP_ID = Terminal(Tokens.ID);
    final Rule OP_DEFINE = Terminal(Tokens.DEFINE);
    final Rule OP_MODEL = Terminal(Tokens.MODEL);

    public Rule Source() {
        final Var<Obj> left = new Var<>();
        final Var<Obj> right = new Var<>();
        final Var<String> operator = new Var<>();
        return ZeroOrMore(
                ACTION(this.currentIndex() <= 0 || left.set((Obj) this.pop())),
                Optional(Obj(left)),
                Sequence(BinaryOperator(), operator.set(match().trim()), Obj(right)),
                ACTION(this.push(operator.isSet() ?
                        OperatorHelper.operation(operator.getAndClear(), left.getAndClear(), right.getAndClear()) :
                        left.getAndClear())));
    }


    @SuppressNode
    Rule Inst() {
        final Var<String> opcode = new Var<>();
        final Var<Obj> value = new Var<>();
        final Var<PList<Obj>> args = new Var<>(new PList<>());
        return Sequence(LBRACKET, VarSym(), opcode.set(match()), ZeroOrMore(COMMA, Obj(value), args.get().add(value.getAndClear())), RBRACKET, this.push(TInst.of(opcode.get(), args.get())));
    }
    ///////////////

    @SuppressNode
    Rule Obj(final Var<Obj> obj) {
        return FirstOf(obj.isSet(), Real(obj), Int(obj), Bool(obj), Str(obj), Sequence(Inst(), obj.set((Obj) this.pop())));
    }

    @SuppressNode
    Rule VarSym() {
        return Sequence(Char(), ZeroOrMore(FirstOf(Char(), Digit())));
    }

    @SuppressNode
    Rule BinaryOperator() {
        return FirstOf(STAR, PLUS, SUB, AND, OR);
    }


    @SuppressNode
    Rule Terminal(final String string) {
        return Sequence(Spacing(), string, Spacing());
    }

    @SuppressNode
    Rule Spacing() {
        return ZeroOrMore(FirstOf(
                // whitespace
                OneOrMore(AnyOf(" \t\r\n\f").label("Whitespace")),
                // traditional comment
                Sequence("/*", ZeroOrMore(TestNot("*/"), ANY), "*/"),
                // end of line comment
                Sequence("//", ZeroOrMore(TestNot(AnyOf("\r\n")), ANY), FirstOf("\r\n", '\r', '\n', EOI))
        ));
    }

   /* Rule Lst(final Var<Obj> object) {
        final Var<PList<Obj>> list = new Var<>(new PList<>());
        return Sequence(LBRACKET,
                FirstOf(SEMICOLON, // empty list
                        Sequence(Entry(), new Action<>() {
                            @Override
                            public boolean run(final Context<Object> context) {
                                return list.get().add((Obj) pop());
                            }
                        }, ZeroOrMore(SEMICOLON, Entry(), new Action<>() {
                            @Override
                            public boolean run(final Context<Object> context) {
                                return list.get().add((Obj) pop());
                            }
                        }))), RBRACKET, object.set(TLst.of(list.get())));
    }

    Rule Rec(final Var<Obj> object) {
        final Var<PMap<Obj, Obj>> rec = new Var<>(new PMap<>());
        return Sequence(LBRACKET,
                FirstOf(COLON, // empty record
                        Sequence(Field(), new Action<>() {
                            @Override
                            public boolean run(final Context<Object> context) {
                                rec.get().put((Obj) pop(), (Obj) pop());
                                return true;
                            }
                        }, ZeroOrMore(COMMA, Field(), new Action<>() {
                            @Override
                            public boolean run(final Context<Object> context) {
                                rec.get().put((Obj) pop(), (Obj) pop());
                                return true;
                            }
                        }))), RBRACKET, object.set(TRec.of(rec.get())));
    }*/

    @SuppressSubnodes
    Rule Real(final Var<Obj> object) {
        return Sequence(Sequence(OneOrMore(Digit()), PERIOD, OneOrMore(Digit())), object.set(TReal.of(Float.valueOf(match()))));
    }

    @SuppressSubnodes
    Rule Int(final Var<Obj> object) {
        return Sequence(OneOrMore(Digit()), object.set(TInt.of(Integer.valueOf(match()))));
    }


    @SuppressSubnodes
    Rule Str(final Var<Obj> object) {
        return FirstOf(
                Sequence(TRIPLE_QUOTE, ZeroOrMore(Sequence(TestNot(TRIPLE_QUOTE), ANY)), object.set(TStr.of(match())), TRIPLE_QUOTE),
                Sequence(SINGLE_QUOTE, ZeroOrMore(Sequence(TestNot(AnyOf("\r\n\\'")), ANY)), object.set(TStr.of(match())), SINGLE_QUOTE),
                Sequence(DOUBLE_QUOTE, ZeroOrMore(Sequence(TestNot(AnyOf("\r\n\"")), ANY)), object.set(TStr.of(match())), DOUBLE_QUOTE));
    }

    @SuppressSubnodes
    Rule Bool(final Var<Obj> object) {
        return Sequence(FirstOf(TRUE, FALSE), object.set(TBool.of(Boolean.valueOf(match()))));
    }

   /* @SuppressSubnodes
    Rule Field() {
        final Var<Obj> key = new Var<>();
        final Var<Obj> value = new Var<>();
        return Sequence(TypeExpress(key), COLON, TypeExpress(value), this.push(key.get()), this.push(value.get()), swap());
    }

    @SuppressSubnodes
    Rule Entry() {
        final Var<Obj> value = new Var<>();
        return Sequence(TypeExpress(value), this.push(value.get()));
    }*/

    @SuppressSubnodes
    Rule Digit() {
        return CharRange('0', '9');
    }

    @SuppressSubnodes
    Rule Char() {
        return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'));
    }
}