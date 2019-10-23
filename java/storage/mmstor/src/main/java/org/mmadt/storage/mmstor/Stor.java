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

package org.mmadt.storage.mmstor;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.TModel;
import org.mmadt.machine.object.model.Model;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.storage.Storage;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class Stor<A extends Obj> implements Storage<A> {

    private static final String SYMBOL = "mmstor";

    private final A root;
    private final Model model;

    public Stor(final A root) { // TODO: make a Model-based constructor
        assert null != root;
        this.root = root;
        this.model = TModel.of(SYMBOL);
        this.model.define(Tokens.DB, ObjectHelper.type(this.root));
    }

    @Override
    public boolean alive() {
        return true;
    }

    @Override
    public A root() {
        return this.root;
    }

    @Override
    public Model model() {
        return this.model;
    }

}
