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

package org.mmadt.language.compiler;

import org.junit.jupiter.api.Test;
import org.mmadt.object.impl.TModel;
import org.mmadt.object.impl.atomic.TInt;
import org.mmadt.object.impl.atomic.TStr;
import org.mmadt.object.impl.composite.TInst;
import org.mmadt.object.impl.composite.TRec;
import org.mmadt.object.model.Obj;
import org.mmadt.object.model.composite.Inst;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.object.impl.composite.TQ.star;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class DefiningTest {

    @Test
    void testDefine() {
        final TModel model = TModel.of("ex");
        final Inst mInst =
                TInst.of("define", "person", TRec.of("name", TStr.some(), "age", TInt.some())).mult(
                        TInst.of("define", "people", model.sym("person").q(star))).mult(
                        TInst.of("define", "db", TRec.of("persons", model.sym("people"))));
        model.model(mInst);
        assertTrue(model.has("person"));
        assertTrue(model.has("people"));
        assertEquals("person", model.sym("person").symbol());
        assertEquals("people", model.sym("people").symbol());
        // assertEquals(2, model.definitions.size());
        assertEquals(TRec.of("name", TStr.some(), "age", TInt.some()).symbol("person"), model.<Obj>get("person"));
        final Inst qInst = TInst.of("db").mult(TInst.of("get", "persons")).mult(TInst.of("get", "name")).mult(TInst.of("is", TInst.of("eq", "marko")));
        RewritingTest.verifyTyping(Rewriting.rewrite(model, qInst));
    }
}