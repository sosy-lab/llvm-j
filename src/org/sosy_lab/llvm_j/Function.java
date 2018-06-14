/*
 * llvm-j  is a library for parsing and modification of LLVM IR in Java.
 * This file is part of llvm-j.
 *
 * Copyright (C) 2012 Kevin Kelly
 * Copyright (C) 2013 Richard Lincoln
 * Copyright (C) 2017 Marek Chalupa, Dirk Beyer
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.sosy_lab.llvm_j;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.sosy_lab.llvm_j.binding.LLVMLibrary;

/** A wrapper around a function value in LLVM */
public final class Function extends Value implements Iterable<BasicBlock> {

  Function(LLVMLibrary.LLVMValueRef value) {
    super(value);
  }

  private class FunctionIterator implements Iterator<BasicBlock> {
    private BasicBlock current;
    private final BasicBlock last;

    FunctionIterator() {
      current = Function.this.getFirstBasicBlock();
      last = Function.this.getLastBasicBlock();
    }

    @Override
    public boolean hasNext() {
      return current != null;
    }

    @Override
    public BasicBlock next() {
      if (hasNext()) {
        BasicBlock tmp = current;
        if (current.equals(last)) {
          current = null;
        } else {
          current = current.getNextBasicBlock();
        }

        return tmp;
      }
      throw new NoSuchElementException();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  @Override
  public Iterator<BasicBlock> iterator() {
    return new FunctionIterator();
  }
}
