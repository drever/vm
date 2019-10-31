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

package org.mmadt.machine.object.impl.composite;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.type.PMap;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.machine.object.model.util.OperatorHelper;
import org.mmadt.machine.object.model.util.StringFactory;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TRec<K extends Obj, V extends Obj> extends TObj implements Rec<K, V> {

    private static final Rec<Obj, Obj> SOME = new TRec<>(null);
    private static final Rec<Obj, Obj> ALL = new TRec<>(null).q(0, Integer.MAX_VALUE);
    private static final Rec<Obj, Obj> NONE = new TRec<>(null).q(0);

    private TRec(final Object value) {
        super(value);
    }

    public static Rec<?, ?> some() {
        return SOME;
    }

    public static Rec<?, ?> all() {
        return ALL;
    }

    public static Rec<?, ?> none() {
        return NONE;
    }

    public static <K extends Obj, V extends Obj> Rec<K, V> of(final PMap<K, V> map) {
        return new TRec<>(map);
    }

    public static <K extends Obj, V extends Obj> Rec<K, V> of(final Object... objects) {
        if (objects.length > 0 && objects[0] instanceof Rec) {
            return ObjectHelper.make(TRec::new, objects);
        } else {
            final PMap<K, V> map = new PMap<>();
            for (int i = 0; i < objects.length; i = i + 2) {
                final K key = (K) ObjectHelper.from(objects[i]);
                final V value = (V) ObjectHelper.from(objects[i + 1]);
                map.put(key, value);
            }
            return new TRec<>(map);
        }
    }

    @Override
    public Rec<K, V> zero() {
        return OperatorHelper.unary(Tokens.ZERO, TRec::of, this);
    }

    @Override
    public Rec<K, V> plus(final Rec<K, V> object) {
        return OperatorHelper.binary(Tokens.PLUS, () -> {
            final PMap<K, V> map = new PMap<>(this.java());
            map.putAll(object.java());
            return new TRec<>(map);
        }, this, object);
    }

    @Override
    public Rec<K, V> minus(final Rec<K, V> object) {
        return OperatorHelper.binary(Tokens.MINUS, () -> {
            final PMap<K, V> map = new PMap<>(this.java());
            object.java().forEach(map::remove);
            return new TRec<>(map);
        }, this, object);
    }

    @Override
    public Rec<K, V> neg() {
        return OperatorHelper.<Rec<K, V>>unary(Tokens.NEG, () -> this, this); // TODO: What is a -rec?
    }

    @Override
    public Bool eq(final Obj obj) {
        return OperatorHelper.binary(Tokens.EQ, () -> TBool.of(obj instanceof Rec && this.java().equals(((Rec) obj).java())), this, obj);
    }

    @Override
    public String toString() {
        return StringFactory.record(this);
    }

}
