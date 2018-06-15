/*
 * llvm-j  is a library for parsing and modification of LLVM IR in Java.
 * This file is part of llvm-j.
 *
 * Copyright (C) 2012 Kevin Kelly
 * Copyright (C) 2013 Richard Lincoln
 * Copyright (C) 2017-2018 Marek Chalupa, Dirk Beyer
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

import com.sun.jna.Pointer;
import javax.annotation.Nullable;
import org.sosy_lab.llvm_j.binding.LLVMLibrary;

/** Util methods for the llvm parser. */
public final class Utils {

  private Utils() {}

  /**
   * Converts the given {@link org.sosy_lab.llvm_j.binding.LLVMLibrary.LLVMBool LLVMBool} value to a
   * Java boolean.
   */
  static boolean llvmBoolToJavaBool(@Nullable LLVMLibrary.LLVMBool pBool) {
    // LLVMBool is actually an int.
    // This is a wrong mapping done by JNA, so we have to convert the PointerType
    // to an integer value.

    // If the native code returns 0, it is (unfortunately) converted to a null pointer.
    // We convert it back.
    if (pBool == null) {
      return false;
    } else {
      Pointer boolAsPointer = pBool.getPointer();
      long bAsInt = Pointer.nativeValue(boolAsPointer);
      assert bAsInt == 0 || bAsInt == 1;
      return bAsInt == 1;
    }
  }

  static void checkLlvmState(boolean pState) {
    if (!pState) {
      throw new IllegalStateException("Invalid state");
    }
  }

  static void checkLlvmState(boolean pState, String pMessage) {
    if (!pState) {
      throw new IllegalStateException(pMessage);
    }
  }
}
