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
     * Create a new context.<br>
     * Every call to this function should be paired with a call to<br>
     * LLVMContextDispose() or the context will leak memnory.<br>
     */
    public static Context create() {
        return new Context(LLVMLibrary.LLVMContextCreate());
    }

    /**
     * Obtain the global context instance.
     */
    public static Context getGlobalContext() {
        return new Context(LLVMLibrary.LLVMGetGlobalContext());
    }

    /**
     * Obtain the context to which this module is associated.<br>
     *
     * @see Module::getContext()
     */
    public static Context getModuleContext(Module m) {
        return new Context(LLVMLibrary.LLVMGetModuleContext(m.getModule()));
    }

    @Override
    public void finalize() {
        dispose();
    }

    /**
     * Destroy a context instance.<br>
     * This should be called for every call to LLVMContextCreate() or memory<br>
     * will be leaked.
     */
    public void dispose() {
        if (context != null) {
            LLVMLibrary.LLVMContextDispose(context);
        }
        context = null;
    }

}
