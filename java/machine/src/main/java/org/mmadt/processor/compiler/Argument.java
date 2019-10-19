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

package org.mmadt.processor.compiler;

import org.mmadt.object.model.Obj;
import org.mmadt.object.model.composite.Inst;

import java.io.Serializable;
import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Argument<S extends Obj, E extends Obj> extends Serializable, Cloneable {

    public E mapArg(final S object);

    public Iterator<E> flatMapArg(final S object);

    public boolean filterArg(final S object);

    public static <S extends Obj, E extends Obj> Argument<S, E> create(final Object arg) {
        return arg instanceof Inst ? new IRArgument<>((Inst) arg) : new ConstantArgument<>((E) arg);
    }

    public Argument<S, E> clone();

}