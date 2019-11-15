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

package org.mmadt.machine.object.model.atomic;

import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.type.algebra.WithMonoidPlus;
import org.mmadt.machine.object.model.type.algebra.WithOrder;
import org.mmadt.processor.util.FastProcessor;

import java.util.List;

/**
 * A Java representation of the {@code str} object in mm-ADT.
 * A {@code str} is an ordered semigroup over +.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Str extends Obj, WithOrder<Str>, WithMonoidPlus<Str> {

    public default String java() {
        return this.get();
    }

    public Bool regex(final Str pattern);

    @Override
    public default Iterable<Str> iterable() {
        return this.isInstance() ? List.of(this) : () -> new FastProcessor<Str>(this.access()).iterator(this);
    }

}
