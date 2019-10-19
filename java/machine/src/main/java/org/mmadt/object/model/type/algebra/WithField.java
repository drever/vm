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

package org.mmadt.object.model.type.algebra;

/**
 * An {@link org.mmadt.object.model.Obj} that supports a +, -, *, and /.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface WithField<A extends WithField<A>> extends WithRing<A>, WithDiv<A> {

    @Override
    public A one();

    @Override
    public A zero();

    @Override
    public A mult(final A object);

    @Override
    public A plus(final A object);

    @Override
    public default A minus(final A object) {
        return this.plus(object.negate());
    }

    @Override
    public A negate();

    @Override
    public default A div(final A object) {
        return this.mult(object.inverse());
    }

    @Override
    public A inverse();
}