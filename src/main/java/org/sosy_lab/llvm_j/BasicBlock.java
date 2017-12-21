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

import static com.google.common.base.Preconditions.checkNotNull;

import com.sun.jna.Pointer;
import java.util.Iterator;
import org.sosy_lab.llvm_j.binding.LLVMLibrary;

/**
 * This represents a single basic block in LLVM. A basic block is simply a container of instructions
 * that execute sequentially.
 */
public final class BasicBlock implements Iterable<Value> {

  private LLVMLibrary.LLVMBasicBlockRef bb;

  LLVMLibrary.LLVMBasicBlockRef bb() {
    return bb;
  }

  BasicBlock(LLVMLibrary.LLVMBasicBlockRef bb) {
    checkNotNull(bb);
    this.bb = bb;
  }

  @Override
  public boolean equals(Object pObj) {
    if (!(pObj instanceof BasicBlock)) {
      return false;
    }
    BasicBlock rhs = (BasicBlock) pObj;
    if (bb == null) {
      return rhs.bb == null;
    } else {
      return rhs.bb.getPointer().equals(bb.getPointer());
    }
  }

  /** Converts this basic block instance to a {@link Value}. */
  public Value basicBlockAsValue() {
    return new Value(LLVMLibrary.LLVMBasicBlockAsValue(bb));
  }

  /** Returns the function to which this basic block belongs. */
  public Value getBasicBlockParent() {
    return new Value(LLVMLibrary.LLVMGetBasicBlockParent(bb));
  }

  /**
   * Returns the next basic block following this one. If this basic block is the last basic block in
   * a function, <code>null</code> is returned.
   */
  public BasicBlock getNextBasicBlock() {
    LLVMLibrary.LLVMBasicBlockRef nextBb = LLVMLibrary.LLVMGetNextBasicBlock(bb);
    if (nextBb == null) {
      return null;
    } else {
      return new BasicBlock(nextBb);
    }
  }

  /**
   * Returns the basic block preceding this one. If this basic block is the first basic block in a
   * function, <code>null</code> is returned.
   */
  public BasicBlock getPreviousBasicBlock() {
    LLVMLibrary.LLVMBasicBlockRef nextBb = LLVMLibrary.LLVMGetPreviousBasicBlock(bb);
    if (nextBb == null) {
      return null;
    } else {
      return new BasicBlock(nextBb);
    }
  }

  /** Returns the first instruction in this basic block. */
  public Value getFirstInstruction() {
    try {
      return new Value(LLVMLibrary.LLVMGetFirstInstruction(bb));
    } catch (java.lang.IllegalArgumentException e) {
      return null;
    }
  }

  /** Returns the last instruction in this basic block. */
  public Value getLastInstruction() {
    try {
      return new Value(LLVMLibrary.LLVMGetLastInstruction(bb));
    } catch (java.lang.IllegalArgumentException e) {
      return null;
    }
  }

  @Override
  public int hashCode() {
    long address = Pointer.nativeValue(bb.getPointer());
    return Long.hashCode(address);
  }

  private class BasicBlockIterator implements Iterator<Value> {
    private Value current;
    private Value last;

    BasicBlockIterator() {
      current = BasicBlock.this.getFirstInstruction();
      last = BasicBlock.this.getLastInstruction();
    }

    @Override
    public boolean hasNext() {
      return current != null;
    }

    @Override
    public Value next() {
      if (hasNext()) {
        Value tmp = current;
        if (current.equals(last)) {
          current = null;
        } else {
          current = current.getNextInstruction();
        }

        return tmp;
      }
      throw new UnsupportedOperationException();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  @Override
  public Iterator<Value> iterator() {
    return new BasicBlockIterator();
  }
}
