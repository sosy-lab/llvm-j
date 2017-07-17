package org.sosy_lab.llvm_j;

import com.sun.jna.Pointer;
import org.sosy_lab.llvm_j.binding.LLVMLibrary;

import java.util.Iterator;

/**
 * This represents a single basic block in LLVM. A basic block is simply a
 * container of instructions that execute sequentially.
 */
public class BasicBlock implements Iterable<Value> {

    private LLVMLibrary.LLVMBasicBlockRef bb;

    LLVMLibrary.LLVMBasicBlockRef bb() {
        return bb;
    }

    BasicBlock(LLVMLibrary.LLVMBasicBlockRef bb) {
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
        return new Value(LLVMLibrary.LLVMBasicBlockAsValue(bb));
    }

    /**
     * Obtain the function to which a basic block belongs.<br>
     *
     * @see llvm::BasicBlock::getParent()
     */
    public Value getBasicBlockParent() {
        return new Value(LLVMLibrary.LLVMGetBasicBlockParent(bb));
    }

    /**
     * Advance a basic block iterator.
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
     * Go backwards in a basic block iterator.
     */
    public BasicBlock getPreviousBasicBlock() {
        LLVMLibrary.LLVMBasicBlockRef nextBb = LLVMLibrary.LLVMGetPreviousBasicBlock(bb);
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
            return new Value(LLVMLibrary.LLVMGetFirstInstruction(bb));
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
            return new Value(LLVMLibrary.LLVMGetLastInstruction(bb));
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
