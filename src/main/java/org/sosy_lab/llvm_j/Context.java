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

import org.sosy_lab.llvm_j.binding.LLVMLibrary;

/**
 * The top-level container for all LLVM global data.
 *
 * <p>After use, each instance of this class should be disposed of using its {@link #dispose()}
 * method.
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
