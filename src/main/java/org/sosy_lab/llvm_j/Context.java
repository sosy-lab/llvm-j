package org.sosy_lab.llvm_j;

import org.sosy_lab.llvm_j.binding.LLVMLibrary;

/**
 * The top-level container for all LLVM global data.
 */
public class Context {

    private LLVMLibrary.LLVMContextRef context;

    LLVMLibrary.LLVMContextRef context() {
        return context;
    }

    Context(LLVMLibrary.LLVMContextRef context) {
        this.context = context;
    }

    /**
     * Creates a new context.<br>
     * Every call to this function should be paired with a call to
     * {@link #dispose()}
     * or the context will leak memory.
     */
    public static Context create() {
        return new Context(LLVMLibrary.LLVMContextCreate());
    }

    /**
     * Returns the global context instance.
     */
    public static Context getGlobalContext() {
        return new Context(LLVMLibrary.LLVMGetGlobalContext());
    }

    /**
     * Returns the context with which a given module is associated.
     */
    public static Context getModuleContext(Module m) {
        return new Context(LLVMLibrary.LLVMGetModuleContext(m.getModule()));
    }

    @Override
    protected void finalize() {
        dispose();
    }

    /**
     * Destroys this context instance.<br>
     * This should be called whenever a {@link Context} instance is not needed anymore,
     * or memory will be leaked.
     */
    public void dispose() {
        if (context != null) {
            LLVMLibrary.LLVMContextDispose(context);
        }
        context = null;
    }

}
