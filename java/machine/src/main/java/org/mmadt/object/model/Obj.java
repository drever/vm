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

package org.mmadt.object.model;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.object.impl.TObj;
import org.mmadt.object.impl.TStream;
import org.mmadt.object.impl.composite.TInst;
import org.mmadt.object.impl.composite.TQ;
import org.mmadt.object.model.atomic.Bool;
import org.mmadt.object.model.composite.Inst;
import org.mmadt.object.model.composite.Q;
import org.mmadt.object.model.type.feature.WithProduct;
import org.mmadt.object.model.type.Bindings;
import org.mmadt.object.model.type.PConjunction;
import org.mmadt.object.model.type.PList;
import org.mmadt.object.model.type.PMap;
import org.mmadt.object.model.type.Pattern;
import org.mmadt.object.model.type.feature.WithAnd;
import org.mmadt.object.model.type.feature.WithOr;
import org.mmadt.object.model.type.feature.WithRing;
import org.mmadt.object.model.util.ObjectHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A Java representation of an mm-ADT {@code obj}.
 * This is the base structure for all mm-ADT objects.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Obj extends Pattern, Cloneable, WithAnd<Obj>, WithOr<Obj> {

    List<Object> TRAMPOLINE = new ArrayList<>(); // TODO: isolate this as its own tool (no more static)

    final Set<String> BASE_SYMBOLS = Set.of(Tokens.OBJ, Tokens.BOOL, Tokens.INT, Tokens.REAL, Tokens.STR, Tokens.LIST, Tokens.REC, Tokens.INST);

    public String symbol();

    public <B> B get();

    public default boolean named() {
        return null != this.symbol() && !BASE_SYMBOLS.contains(this.symbol());
    }

    public <B extends WithRing<B>> Q<B> q();

    public String variable();

    public Inst access();

    public PMap<Inst, Inst> instructions();

    public PMap<Obj, Obj> members();

    public Bool eq(final Obj object);

    public default <O extends Obj> O peak() {
        return null == this.get() ? (O) TObj.none() : this.get() instanceof Stream ? ((Stream<O>) this.get()).peak() : (O) this;
    }

    public default <O extends Obj> O last() {
        return null == this.get() ? (O) TObj.none() : this.get() instanceof Stream ? ((Stream<O>) this.get()).last() : (O) this;
    }

    public <O extends Obj> O type(final O type);

    public Obj type();

    public default Iterable<? extends Obj> iterable() {
        return this.q().isZero() ? List.of() : TStream.of(this);
    }

    public <O extends Obj> O push(final O obj);

    public <O extends Obj> O pop();

    public <O extends Obj> O set(final Object object);

    public <O extends Obj> O q(final Q quantifier);

    public default <O extends Obj> O q(final Q.Tag tag) {
        return this.q(tag.apply(this.q()));
    }


    public default <O extends Obj> O q(final int low, final int high) {
        return this.q(new TQ<>(low, high));
    }

    public default <O extends Obj> O q(final int count) {
        return this.q(count, count);
    }

    public default <O extends Obj> O q(final WithRing count) {
        return this.q(new TQ<>(count));
    }

    public <O extends Obj> O as(final String variable);

    public <O extends Obj> O access(final Inst access);

    public <O extends Obj> O inst(final Inst instA, final Inst instB);

    public <O extends Obj> O symbol(final String symbol);

    public <O extends Obj> O insts(final PMap<Inst, Inst> insts);

    @Override
    public default Obj bind(final Bindings bindings) {
        if (bindings.has(this.variable()))
            return bindings.get(this.variable());
        return this.insts(null == this.instructions() ? null : this.instructions().bind(bindings)).
                set(this.get() instanceof Pattern ? ((Pattern) this.get()).bind(bindings) : this.get()).
                access(this.access().equals(TInst.none()) ? null : (Inst) this.access().bind(bindings));
    }

    @Override
    public default boolean test(final Obj object) {
        boolean root = TRAMPOLINE.isEmpty();
        if (TRAMPOLINE.contains(List.of(this, object)))
            return true;
        else
            TRAMPOLINE.add(List.of(this, object));
        try {
            if (TObj.none().equals(this) || TObj.none().equals(object))
                return this.q().test(object);
            else if (null != ObjectHelper.getName(this) && ObjectHelper.getName(this).equals(ObjectHelper.getName(object)))
                return true;
            else {
                final Object current = this.get();
                if (object.get() instanceof Stream)
                    return Stream.testStream(this, object);
                else if (this.get() instanceof PList && object.get() instanceof PList && !(this instanceof Inst))
                    return Stream.testStream(this.set(TStream.of(this.<PList>get())), object.set(TStream.of(object.<PList>get())));
                else if (null != current)
                    return current instanceof Pattern ? ((Pattern) current).test(object) : current.equals(object.get());
                else
                    return ObjectHelper.isSubClassOf(this, object);
            }
        } finally {
            if (root)
                TRAMPOLINE.clear();
        }
    }

    @Override
    public default boolean match(final Bindings bindings, final Obj object) {
        if (bindings.has(this.variable()))
            return bindings.get(this.variable()).test(object);
        else if (TObj.none().equals(this) || TObj.none().equals(object))
            return this.q().test(object);
        bindings.start();
        final Object current = this.get();
        if (null != current) {
            if (object.get() instanceof Stream) {
                if (!Stream.matchStream(bindings, this, object)) {
                    bindings.rollback();
                    return false;
                }
            } else if (current instanceof Pattern) {
                if (!((Pattern) current).match(bindings, object)) {
                    bindings.rollback();
                    return false;
                }
            } else if (!this.test(object)) {
                bindings.rollback();
                return false;
            }
        } else if (!ObjectHelper.isSubClassOf(this, object)) {
            bindings.rollback();
            return false;
        }
        if (null != this.variable())
            bindings.put(this.variable(), object);
        if (this instanceof WithProduct)
            bindings.commit();
        return true;
    }

    public default Optional<Inst> inst(final Bindings bindings, final Inst inst) {
        ObjectHelper.members(this, bindings);
        if (null != this.instructions()) {
            for (final Map.Entry<Inst, Inst> entry : this.instructions().entrySet()) {
                if (entry.getKey().match(bindings, inst))
                    return Optional.of((Inst) entry.getValue().bind(bindings));
            }
        }
        if (null != this.type() && this.type().get() instanceof PConjunction) // TODO: this is why having a specialized variant class is important (so the logic is more recursive)
            return ((PConjunction) this.type().get()).inst(this, bindings, inst);
        else if (this.get() instanceof PConjunction)
            return ((PConjunction) this.get()).inst(this, bindings, inst);
        else
            return Optional.empty();
    }

    //////////////

    public default boolean isType() {
        return !this.constant() && (TObj.none().test(this.access()) || !this.access().constant());
    }

    public default boolean isReference() {
        return !this.constant() && (!TObj.none().test(this.access()) && this.access().constant());
    }

    public default boolean isInstance() {
        return this.constant() && (TObj.none().test(this.access()) || this.access().constant());
    }

    public Obj clone();

}