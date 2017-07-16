package org.llvm;

import com.sun.jna.Pointer;

import java.util.Iterator;

import static org.llvm.binding.LLVMLibrary.*;

/**
 * This represents a single basic block in LLVM. A basic block is simply a
 * container of instructions that execute sequentially.
 */
public class BasicBlock implements Iterable<Value> {

    private LLVMBasicBlockRef bb;

    LLVMBasicBlockRef bb() {
        return bb;
    }

    BasicBlock(LLVMBasicBlockRef bb) {
        this.bb = bb;
    }

    @Override
    public boolean equals(final Object pObj) {
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

    /**
     * Convert a basic block instance to a value type.
     */
    public Value basicBlockAsValue() {
        return new Value(LLVMBasicBlockAsValue(bb));
    }

    /**
     * Obtain the function to which a basic block belongs.<br>
     *
     * @see llvm::BasicBlock::getParent()
     */
    public Value getBasicBlockParent() {
        return new Value(LLVMGetBasicBlockParent(bb));
    }

    /**
     * Advance a basic block iterator.
     */
    public BasicBlock getNextBasicBlock() {
        LLVMBasicBlockRef nextBb = LLVMGetNextBasicBlock(bb);
        if (nextBb == null) {
            return null;
        } else {
            return new BasicBlock(nextBb);
        }
    }

    /**
     * Go backwards in a basic block iterator.
     */
    public BasicBlock getPreviousBasicBlock() {
        LLVMBasicBlockRef nextBb = LLVMGetPreviousBasicBlock(bb);
        if (nextBb == null) {
            return null;
        } else {
            return new BasicBlock(nextBb);
        }
    }

    /**
     * Obtain the first instruction in a basic block.<br>
     * The returned LLVMValueRef corresponds to a llvm::Instruction<br>
     * instance.
     */
    public Value getFirstInstruction() {
        try {
            return new Value(LLVMGetFirstInstruction(bb));
        } catch (java.lang.IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Obtain the last instruction in a basic block.<br>
     * The returned LLVMValueRef corresponds to a LLVM:Instruction.
     */
    public Value getLastInstruction() {
        try {
            return new Value(LLVMGetLastInstruction(bb));
        } catch (java.lang.IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public int hashCode() {
        final long address = Pointer.nativeValue(bb.getPointer());
        return Long.hashCode(address);
    }

    private class BasicBlockIterator implements Iterator<Value> {
        private Value current;
        private Value last;

        public BasicBlockIterator() {
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
                if (current.equals(last))
                    current = null;
                else
                    current = current.getNextInstruction();

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
