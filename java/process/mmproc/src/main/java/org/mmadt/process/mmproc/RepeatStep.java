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
import org.mmadt.processor.compiler.IR;
import org.mmadt.processor.function.branch.RepeatBranch;
import org.mmadt.processor.util.ObjSet;

import java.util.Collections;
import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class RepeatStep<S extends Obj> extends AbstractStep<S, S> {

    private final RepeatBranch<S> repeatBranch;
    private final int untilLocation;
    private final int emitLocation;
    private final IR<S, ?> untilCompilation;
    private final IR<S, ?> emitCompilation;
    private final IR<S, S> repeat;
    private Iterator<S> repeatIterator = Collections.emptyIterator();
    private ObjSet<S> repeatStarts = new ObjSet<>();
    private ObjSet<S> outputTraversers = new ObjSet<>();
    private ObjSet<S> inputTraversers = new ObjSet<>();
    private final boolean hasStartPredicates;
    private final boolean hasEndPredicates;

    RepeatStep(final Step<?, S> previousStep, final RepeatBranch<S> repeatBranch) {
        super(previousStep, repeatBranch);
        this.repeatBranch = repeatBranch;
        this.untilCompilation = repeatBranch.getUntil();
        this.emitCompilation = repeatBranch.getEmit();
        this.repeat = repeatBranch.getRepeat();
        this.untilLocation = repeatBranch.getUntilLocation();
        this.emitLocation = repeatBranch.getEmitLocation();
        this.hasStartPredicates = repeatBranch.hasStartPredicates();
        this.hasEndPredicates = repeatBranch.hasEndPredicates();
    }

    @Override
    public boolean hasNext() {
        this.stageOutput();
        return false;//!this.outputTraversers.isEmpty();
    }

    @Override
    public S next() {
        this.stageOutput();
        return null; //this.outputTraversers.remove();
    }

    private boolean stageInput() {
        /*if (!this.inputTraversers.isEmpty() || this.previousStep.hasNext()) {
            final Traverser<C, S> traverser = this.inputTraversers.isEmpty() ? this.previousStep.next() : this.inputTraversers.remove();
            if (this.hasStartPredicates) {
                if (1 == this.untilLocation) {
                    if (this.untilCompilation.filterTraverser(traverser)) {
                        this.outputTraversers.add(traverser);
                    } else if (2 == this.emitLocation && this.emitCompilation.filterTraverser(traverser)) {
                        this.outputTraversers.add(traverser.repeatDone(this.repeatBranch));
                        this.repeatStarts.add(traverser);
                    } else
                        this.repeatStarts.add(traverser);
                } else if (1 == this.emitLocation) {
                    if (this.emitCompilation.filterTraverser(traverser))
                        this.outputTraversers.add(traverser.repeatDone(this.repeatBranch));
                    if (2 == this.untilLocation && this.untilCompilation.filterTraverser(traverser))
                        this.outputTraversers.add(traverser.repeatDone(this.repeatBranch));
                    else
                        this.repeatStarts.add(traverser);
                }
            } else {
                this.repeatStarts.add(traverser);
            }
            return true;
        }*/
        return false;
    }

    private void stageOutput() {
       /* while (this.outputTraversers.isEmpty() && (this.repeatIterator.hasNext() || this.stageInput())) {
            if (this.repeatIterator.hasNext()) {
                final Traverser<C, S> traverser = this.repeatIterator.next().repeatLoop(this.repeatBranch);
                if (this.hasEndPredicates) {
                    if (3 == this.untilLocation) {
                        if (this.untilCompilation.filterTraverser(traverser)) {
                            this.outputTraversers.add(traverser.repeatDone(this.repeatBranch));
                        } else if (4 == this.emitLocation && this.emitCompilation.filterTraverser(traverser)) {
                            this.outputTraversers.add(traverser.repeatDone(this.repeatBranch));
                            this.inputTraversers.add(traverser);
                        } else
                            this.inputTraversers.add(traverser);
                    } else if (3 == this.emitLocation) {
                        if (this.emitCompilation.filterTraverser(traverser))
                            this.outputTraversers.add(traverser.repeatDone(this.repeatBranch));
                        if (4 == this.untilLocation && this.untilCompilation.filterTraverser(traverser))
                            this.outputTraversers.add(traverser.repeatDone(this.repeatBranch));
                        else
                            this.inputTraversers.add(traverser);
                    }
                } else {
                    this.inputTraversers.add(traverser);
                }
            } else {
                this.repeatIterator = this.repeat.getProcessor().iterator(IteratorUtils.removeOnNext(this.repeatStarts.iterator()));
            }
        }*/
    }

    @Override
    public void reset() {
        // this.inputTraversers.clear();
        // this.outputTraversers.clear();
    }
}