package org.sosy_lab.llvm_j;

import com.sun.jna.Pointer;
import org.sosy_lab.llvm_j.binding.LLVMLibrary;

/** Util methods for the llvm parser. */
public final class Utils {

  private Utils() {}

  /**
   * Converts the given {@link org.sosy_lab.llvm_j.binding.LLVMLibrary.LLVMBool LLVMBool} value to a
   * Java boolean.
   */
  public static boolean llvmBoolToJavaBool(LLVMLibrary.LLVMBool pBool) {
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
}
