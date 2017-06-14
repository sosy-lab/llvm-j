package org.llvm;

import java.util.Iterator;
import org.bridj.Pointer;

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

    public long getAddress() {
        return bb.getPeer();
    }

    public boolean equals(BasicBlock rhs) {
        return getAddress() == rhs.getAddress();
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
        return new BasicBlock(LLVMGetNextBasicBlock(bb));
    }

    /**
     * Go backwards in a basic block iterator.
     */
    public BasicBlock getPreviousBasicBlock() {
        return new BasicBlock(LLVMGetPreviousBasicBlock(bb));
    }

    /**
     * Insert a new basic block before this basic block, and return it.
     */
    public BasicBlock insertBasicBlock(String name) {
        return new BasicBlock(LLVMInsertBasicBlock(bb,
                Pointer.pointerToCString(name)));
    }

    /**
     * Insert a new basic block before this basic block, and return it
     */
    public BasicBlock InsertBasicBlockInContext(Context c, String name) {
        return new BasicBlock(LLVMInsertBasicBlockInContext(c.context(), bb,
                Pointer.pointerToCString(name)));
    }

    /**
     * Remove a basic block from a function and delete it.<br>
     * This deletes the basic block from its containing function and deletes<br>
     * the basic block itself.<br>
     *
     * @see llvm::BasicBlock::eraseFromParent()
     */
    public void deleteBasicBlock() {
        LLVMDeleteBasicBlock(bb);
    }

    /**
     * Move a basic block to before another one.<br>
     *
     * @see llvm::BasicBlock::moveBefore()
     */
    public void moveBasicBlockBefore(BasicBlock movePos) {
        LLVMMoveBasicBlockBefore(bb, movePos.bb());
    }

    /**
     * Move a basic block to after another one.<br>
     *
     * @see llvm::BasicBlock::moveAfter()
     */
    public void moveBasicBlockAfter(BasicBlock movePos) {
        LLVMMoveBasicBlockAfter(bb, movePos.bb());
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

    private class BasicBlockIterator implements Iterator<Value> {
        private Value current;
        private Value last;

        public BasicBlockIterator() {
            current = BasicBlock.this.getFirstInstruction();
            last = BasicBlock.this.getLastInstruction();
        }

        public boolean hasNext() {
            return current != null;
        }

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

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public Iterator<Value> iterator() {
        return new BasicBlockIterator();
    }
}
