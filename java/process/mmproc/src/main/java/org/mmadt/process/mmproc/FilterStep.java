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

package org.mmadt.process.mmproc;

import org.mmadt.object.model.Obj;
import org.mmadt.processor.function.FilterFunction;
import org.mmadt.util.FastNoSuchElementException;
import org.mmadt.util.FunctionUtils;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class FilterStep<S extends Obj> extends AbstractStep<S, S> {

    private final FilterFunction<S> filterFunction;
    private S nextObj = null;

    FilterStep(final Step<?, S> previousStep, final FilterFunction<S> filterFunction) {
        super(previousStep, filterFunction);
        this.filterFunction = filterFunction;
    }

    @Override
    public S next() {
        this.stageNextObj();
        if (null == this.nextObj)
            throw FastNoSuchElementException.instance();
        else {
            final S traverser = this.nextObj;
            this.nextObj = null;
            return traverser;
        }
    }

    @Override
    public boolean hasNext() {
        this.stageNextObj();
        return null != this.nextObj;
    }

    private void stageNextObj() {
        while (null == this.nextObj && this.previousStep.hasNext()) {
            FunctionUtils.test(this.filterFunction,this.previousStep.next()).ifPresent(traverser -> this.nextObj = traverser);
        }
    }

    @Override
    public void reset() {
        this.nextObj = null;
    }
}