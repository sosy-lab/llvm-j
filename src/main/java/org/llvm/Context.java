package org.llvm;

import org.llvm.binding.LLVMLibrary.*;

import static org.llvm.binding.LLVMLibrary.*;

/**
 * The top-level container for all LLVM global data.
 */
public class Context {

    private LLVMContextRef context;

    LLVMContextRef context() {
        return context;
    }

    Context(LLVMContextRef context) {
        this.context = context;
    }

    /**
     * Create a new context.<br>
     * Every call to this function should be paired with a call to<br>
     * LLVMContextDispose() or the context will leak memnory.<br>
     */
    public static Context create() {
        return new Context(LLVMContextCreate());
    }

    /**
     * Obtain the global context instance.
     */
    public static Context getGlobalContext() {
        return new Context(LLVMGetGlobalContext());
    }

    /**
     * Obtain the context to which this module is associated.<br>
     *
     * @see Module::getContext()
     */
    public static Context getModuleContext(Module m) {
        return new Context(LLVMGetModuleContext(m.getModule()));
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
            LLVMContextDispose(context);
        }
        context = null;
    }

}
