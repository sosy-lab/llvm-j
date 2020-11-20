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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.errorprone.annotations.Var;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeMappedConverter;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import java.io.Closeable;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.sosy_lab.llvm_j.binding.LLVMLibrary;

/**
 * A compilation unit of the LLVM Intermediate Representation (LLVM IR).
 *
 * <p>Resources of this class always have to be freed using {@link #close()} to avoid memory leaks.
 * It is advised to use the try-with construct to ensure this.
 */
// Suppress the warning about JavaLangClash, as we really want this class to be named Module as
// in the C++ LLVM API.
@SuppressWarnings("JavaLangClash")
public final class Module implements Iterable<Value>, Closeable {

  private LLVMLibrary.LLVMModuleRef module;
  private String fileName;

  LLVMLibrary.LLVMModuleRef getModule() {
    return module;
  }

  private Module(LLVMLibrary.LLVMModuleRef pModule, String pFileName) {
    module = pModule;
    fileName = pFileName;
  }

  private Module(LLVMLibrary.LLVMModuleRef pModule) {
    module = pModule;
  }

  /**
   * Adds the given directories to the list of paths in which llvm-j looks for the LLVM library.
   *
   * @param pDirectories list of directories that may contain the LLVM library
   */
  // FIXME: Is this really the right location for this method?
  public static void addLibraryLookupPaths(List<Path> pDirectories) {
    checkNotNull(pDirectories);
    for (Path p : pDirectories) {
      NativeLibrary.addSearchPath(LLVMLibrary.JNA_LIBRARY_NAME, p.toAbsolutePath().toString());
    }
  }

  /**
   * Parses a module from the given file. Both LLVM bitcode and human-readable LLVM IR are accepted.
   *
   * <p>This method has to instantiate {@link LLVMLibrary}. If your LLVM shared library (*.so file)
   * is not in one of the default JNA library search paths (e.g., system directories, directories
   * specified by system property jna.library.path), it can only be found if the directory it is in
   * is added to the search paths using {@link #addLibraryLookupPaths(List)} before calling this
   * method.
   *
   * @param path the LLVM IR file to parse
   * @return the parsed LLVM module structure
   * @deprecated this method creates a {@link Context} instance and never disposes of it, thus
   *     creating a memory leak.
   */
  @Deprecated
  @SuppressWarnings("resource")
  public static Module parseIR(String path) throws LLVMException {
    Context context = Context.create();
    return parseIR(path, context);
  }

  /**
   * Parses a module from the given file. Both LLVM bitcode and human-readable LLVM IR are accepted.
   *
   * <p>This method has to instantiate {@link LLVMLibrary}. If your LLVM shared library (*.so file)
   * is not in one of the default JNA library search paths (e.g., system directories, directories
   * specified by system property jna.library.path), it can only be found if the directory it is in
   * is added to the search paths using {@link #addLibraryLookupPaths(List)} before calling this
   * method.
   *
   * @param path the LLVM IR file to parse
   * @param pContext {@link Context} to use for parsing
   * @return the parsed LLVM module structure
   */
  @SuppressWarnings("deprecation")
  public static Module parseIR(String path, Context pContext) throws LLVMException {
    checkNotNull(path);
    checkNotNull(pContext);

    @Var LLVMLibrary.LLVMContextRef context = pContext.context();
    long messageBufferLength = 1000 * 1000; // bytes
    Pointer outMsgAddr = new Memory(messageBufferLength);

    /* read the module into a buffer */
    PointerByReference pointerToBuffer = new PointerByReference();
    LLVMLibrary.LLVMMemoryBufferRef pointerToBufferWrapped =
        new LLVMLibrary.LLVMMemoryBufferRef(pointerToBuffer.getPointer());
    PointerByReference outMsg = new PointerByReference(outMsgAddr);
    @Var
    LLVMLibrary.LLVMBool success =
        LLVMLibrary.LLVMCreateMemoryBufferWithContentsOfFile(path, pointerToBufferWrapped, outMsg);
    if (Utils.llvmBoolToJavaBool(success)) {
      String errorMessage = refToString(outMsg);
      throw new LLVMException("Reading bitcode failed. " + errorMessage);
    }
    LLVMLibrary.LLVMMemoryBufferRef buffer =
        new LLVMLibrary.LLVMMemoryBufferRef(pointerToBuffer.getValue());

    /* create a module from the memory buffer */
    long moduleRefSize = getSize(LLVMLibrary.LLVMModuleRef.class);
    PointerByReference pointerToModule = new PointerByReference(new Memory(moduleRefSize));
    LLVMLibrary.LLVMModuleRef pointerToModuleWrapped =
        new LLVMLibrary.LLVMModuleRef(pointerToModule.getPointer());

    if (path.endsWith(".bc")) {
      success = LLVMLibrary.LLVMParseBitcodeInContext2(context, buffer, pointerToModuleWrapped);
      if (Utils.llvmBoolToJavaBool(success)) {
        throw new LLVMException("Parsing bitcode failed");
      }

      /* free the buffer allocated by readFileToBuffer */
      // FIXME: This returns a segfault when done for LLVMParseIRInContext. Why?
      LLVMLibrary.LLVMDisposeMemoryBuffer(buffer);
    } else {
      success = LLVMLibrary.LLVMParseIRInContext(context, buffer, pointerToModuleWrapped, outMsg);
      if (Utils.llvmBoolToJavaBool(success)) {
        throw new LLVMException(
            "Parsing bitcode (human-readable format) failed. " + refToString(outMsg));
      }
    }

    LLVMLibrary.LLVMModuleRef module = new LLVMLibrary.LLVMModuleRef(pointerToModule.getValue());

    return new Module(module, path);
  }

  private static String refToString(PointerByReference pRef) {
    return pRef.getValue().getString(0);
  }

  private static long getSize(Class<?> pClass) {
    Class<?> nativeClass = NativeMappedConverter.getInstance(pClass).nativeType();
    return Native.getNativeSize(nativeClass);
  }

  /**
   * Creates a new, empty module in the global context.<br>
   * Every invocation should be paired with {link #close()} or memory will be leaked.
   *
   * <p>To avoid memory leaks, a model always has to be disposed of using {@link #close()} after
   * use.
   *
   * @param moduleID the name of the new module
   */
  public static Module createWithName(String moduleID) {
    checkNotNull(moduleID);
    return new Module(LLVMLibrary.LLVMModuleCreateWithName(moduleID));
  }

  /**
   * Creates a new, empty module in a specific context.<br>
   * Every invocation should be paired with {@link #close()} or memory will be leaked.
   *
   * <p>To avoid memory leaks, a model always has to be disposed of using {@link #close()} after
   * use.
   *
   * @param moduleID the name of the new module
   * @param c the context to create the new module in
   */
  public static Module createWithNameInContext(String moduleID, Context c) {
    checkNotNull(moduleID);
    checkNotNull(c);
    return new Module(LLVMLibrary.LLVMModuleCreateWithNameInContext(moduleID, c.context()));
  }

  /**
   * Creates a module representing the global parent of the given {@link Value}.
   *
   * <p>To avoid memory leaks, a model always has to be disposed of using {@link #close()} after
   * use.
   */
  public static Module createGlobalParentOf(Value pValue) {
    return new Module(LLVMLibrary.LLVMGetGlobalParent(pValue.value()));
  }

  /**
   * Destroys this module instance.<br>
   * This must be called for every created module or memory will be leaked.
   *
   * @deprecated Use {@link #close()} instead
   */
  @Deprecated
  public void dispose() {
    close();
  }

  /** Returns the origin of this module, i.e., its source file name. */
  public @Nullable String getOriginFileName() {
    return fileName;
  }

  /** Returns the data layout string for this module. */
  public String getDataLayoutString() {
    return LLVMLibrary.LLVMGetDataLayout(module);
  }

  /** Returns the data layout object for this module. */
  public LLVMLibrary.LLVMTargetDataRef getDataLayout() {
    return LLVMLibrary.LLVMCreateTargetData(getDataLayoutString());
  }

  /** Returns the target triple for this module. */
  public String getTargetString() {
    return LLVMLibrary.LLVMGetTarget(module);
  }

  /*public int addTypeName(String name, LLVMTypeRef ty) {
      Pointer<Byte> cstr = Pointer.pointerToCString(name);
      return LLVMAddTypeName(module, cstr, ty);
  }

  public void deleteTypeName(String name) {
      Pointer<Byte> cstr = Pointer.pointerToCString(name);
      LLVMDeleteTypeName(module, cstr);
  }*/

  /** Returns a {@link TypeRef} from this module by its registered name. */
  public TypeRef getTypeByName(String name) {
    checkNotNull(name);
    return new TypeRef(LLVMLibrary.LLVMGetTypeByName(module, name));
  }

  /*public String getTypeName(LLVMTypeRef ty) {
      Pointer<Byte> cstr = LLVMGetTypeName(module, ty);
      return cstr.getCString();
  }*/

  /** Dumps a representation of this module to stderr. */
  public void dump() {
    LLVMLibrary.LLVMDumpModule(module);
  }

  /**
   * Writes this module to the specified path.
   *
   * @return returns 0 on success, an error code otherwise.
   */
  public int writeBitcodeToFile(String path) {
    checkNotNull(path);
    return LLVMLibrary.LLVMWriteBitcodeToFile(module, path);
  }

  /** Returns the module context. */
  public Context getModuleContext() {
    return Context.getModuleContext(this);
  }

  /** Returns the named global in this module with the given name. */
  public Value getNamedGlobal(String name) {
    checkNotNull(name);
    return new Value(LLVMLibrary.LLVMGetNamedGlobal(getModule(), name));
  }

  /** Returns the first global value in this module. */
  public Value getFirstGlobal() {
    try {
      LLVMLibrary.LLVMValueRef ref = LLVMLibrary.LLVMGetFirstGlobal(getModule());
      if (ref != null) {
        return new Value(ref);
      } else {
        return null;
      }
    } catch (java.lang.IllegalArgumentException e) {
      return null;
    }
  }

  /** Returns the last global value in this module. */
  public Value getLastGlobal() {
    try {
      return new Value(LLVMLibrary.LLVMGetLastGlobal(getModule()));
    } catch (java.lang.IllegalArgumentException e) {
      return null;
    }
  }

  /**
   * Returns a new alias for the given type and adds it to the end of the modules alias list.
   *
   * @param ty type of the value to create the alias for
   * @param aliasee value to create the alias for
   * @param name name of the new alias
   * @return the newly created alias for the given value
   */
  public Value addAlias(TypeRef ty, Value aliasee, String name) {
    checkNotNull(ty);
    checkNotNull(aliasee);
    checkNotNull(name);
    return new Value(LLVMLibrary.LLVMAddAlias(module, ty.type(), aliasee.value(), name));
  }

  /** Returns a {@link Function} from this module by its name. */
  public Function getNamedFunction(String name) {
    checkNotNull(name);
    return new Function(LLVMLibrary.LLVMGetNamedFunction(module, name));
  }

  /** Returns an iterator to the first Function in this module. */
  public Value getFirstFunction() {
    try {
      return new Value(LLVMLibrary.LLVMGetFirstFunction(module));
    } catch (java.lang.IllegalArgumentException e) {
      return null;
    }
  }

  /** Returns an iterator to the last Function in this module. */
  public Value getLastFunction() {
    try {
      return new Value(LLVMLibrary.LLVMGetLastFunction(module));
    } catch (java.lang.IllegalArgumentException e) {
      return null;
    }
  }

  /**
   * Destroys this module instance.<br>
   * This must be called for every created module or memory will be leaked.
   */
  @Override
  public void close() {
    LLVMLibrary.LLVMDisposeModule(module);
    module = null;
  }

  private class ModuleIterator implements Iterator<Value> {
    private Value current;
    private final Value last;

    ModuleIterator() {
      current = Module.this.getFirstFunction();
      last = Module.this.getLastFunction();
    }

    @Override
    public boolean hasNext() {
      return current != null;
    }

    @Override
    public Value next() {
      if (hasNext()) {
        Value tmp = current;
        if (current.equals(last)) {
          current = null;
        } else {
          current = current.getNextFunction();
        }
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
    return new ModuleIterator();
  }
}
