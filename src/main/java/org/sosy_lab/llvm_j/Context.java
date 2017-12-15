package org.sosy_lab.llvm_j;

import org.sosy_lab.llvm_j.binding.LLVMLibrary;

/**
 * The top-level container for all LLVM global data.
 *
 * <p>Each instance of this class should be disposed of using its {@link #dispose()} method after
 * use.
 */
public final class Context {

  private LLVMLibrary.LLVMContextRef context;

  LLVMLibrary.LLVMContextRef context() {
    return context;
  }

  private Context(LLVMLibrary.LLVMContextRef context) {
    this.context = context;
  }

  /**
   * Creates a new context.
   *
   * <p>Every call to this function should be paired with a call to {@link #dispose()} or the
   * context will leak memory.
   */
  public static Context create() {
    return new Context(LLVMLibrary.LLVMContextCreate());
  }

  /**
   * Returns the global context instance.
   *
   * <p>Every call to this function should be paired with a call to {@link #dispose()} or the
   * context will leak memory.
   */
  public static Context getGlobalContext() {
    return new Context(LLVMLibrary.LLVMGetGlobalContext());
  }

  /**
   * Returns the context with which a given module is associated.
   *
   * <p>Every call to this function should be paired with a call to {@link #dispose()} or the
   * context will leak memory.
   */
  public static Context getModuleContext(Module m) {
    return new Context(LLVMLibrary.LLVMGetModuleContext(m.getModule()));
  }

  /**
   * Returns the context with which a given {@link TypeRef type} is associated.
   *
   * <p>Every call to this function should be paired with a call to {@link #dispose()} or the
   * context will leak memory.
   */
  public static Context getTypeContext(TypeRef pType) {
    return new Context(LLVMLibrary.LLVMGetTypeContext(pType.type()));
  }

  /**
   * Destroys this context instance. This should be called whenever a {@link Context} instance is
   * not needed anymore, or memory will be leaked.
   */
  public void dispose() {
    if (context != null) {
      LLVMLibrary.LLVMContextDispose(context);
    }
    context = null;
  }
}
