package org.llvm;

import static org.llvm.binding.LLVMLibrary.LLVMValueRef;

import java.util.Iterator;

/**
 * A wrapper around a function value in LLVM
 */
public class Function extends Value implements Iterable<BasicBlock> {
    Function(LLVMValueRef value) {
        super(value);
    }

    private class FunctionIterator implements Iterator<BasicBlock> {
        private BasicBlock current;
        private BasicBlock last;

        public FunctionIterator() {
            current = Function.this.getFirstBasicBlock();
            last = Function.this.getLastBasicBlock();
        }

        public boolean hasNext() {
            return current != null;
        }

        public BasicBlock next() {
            if (hasNext()) {
                BasicBlock tmp = current;
                if (current.equals(last))
                    current = null;
                else
                    current = current.getNextBasicBlock();

                return tmp;
            }
            throw new UnsupportedOperationException();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public Iterator<BasicBlock> iterator() {
        return new FunctionIterator();
    }
}