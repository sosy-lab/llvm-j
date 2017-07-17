package org.sosy_lab.llvm_j;

import com.sun.jna.*;
import com.sun.jna.ptr.PointerByReference;
import org.sosy_lab.llvm_j.binding.LLVMLibrary;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

/**
 * The main container class for the LLVM Intermediate Representation.
 */
public class Module implements Iterable<Value> {
    private LLVMLibrary.LLVMModuleRef module;

    LLVMLibrary.LLVMModuleRef getModule() {
        return module;
    }

    Module(LLVMLibrary.LLVMModuleRef module) {
        this.module = module;
    }

    public static void addLibraryLookupPaths(final List<Path> pDirectories) {
        for (Path p : pDirectories) {
            NativeLibrary.addSearchPath(LLVMLibrary.JNA_LIBRARY_NAME, p.toAbsolutePath().toString());
        }
    }

    /**
     * Parse a module from file
     */
    public static Module parseIR(String path) {
        LLVMLibrary.instantiate();
        /* read the module into a buffer */
        //Pointer address = new Memory(1000*1000*8);
        //LLVMMemoryBufferRef buffer = new LLVMMemoryBufferRef(address);

        PointerByReference pointerToBuffer = new PointerByReference();
        LLVMLibrary.LLVMMemoryBufferRef pointerToBufferWrapped = new LLVMLibrary.LLVMMemoryBufferRef(pointerToBuffer.getPointer());
        //LLVMMemoryBufferRef buffer = LLVMCreateMemoryBufferWithMemoryRange(fileContent, new NativeSize(800 * 1000 * 1000), "test", no);
        Pointer outMsgAddr = new Memory(1000 * 1000 * 10 * 8);
        PointerByReference outMsg = new PointerByReference(outMsgAddr);
        LLVMLibrary.LLVMBool success = LLVMLibrary.LLVMCreateMemoryBufferWithContentsOfFile(path, pointerToBufferWrapped, outMsg);
        LLVMLibrary.LLVMMemoryBufferRef buffer = new LLVMLibrary.LLVMMemoryBufferRef(pointerToBuffer.getValue());
        // if (!Utils.llvmBoolToJavaBool(success)) {
        //    System.err.println("Reading bitcode failed\n");
        //    return null;
        // }

    /* create a module from the memory buffer */
        PointerByReference pointerToModule = new PointerByReference(new Memory(getSize(LLVMLibrary.LLVMModuleRef.class)));
        LLVMLibrary.LLVMModuleRef pointerToModuleWrapped = new LLVMLibrary.LLVMModuleRef(pointerToModule.getPointer());
        success = LLVMLibrary.LLVMParseBitcode2(buffer, pointerToModuleWrapped);
        //if (!Utils.llvmBoolToJavaBool(success)) {
        //    return null;
        //}
        LLVMLibrary.LLVMModuleRef module = new LLVMLibrary.LLVMModuleRef(pointerToModule.getValue());
        /* free the buffer allocated by readFileToBuffer */
        LLVMLibrary.LLVMDisposeMemoryBuffer(buffer);

        return new Module(module);
    }

    private static long getSize(Class<?> pClass) {
        Class<?> nativeClass = NativeMappedConverter.getInstance(pClass).nativeType();
        return Native.getNativeSize(nativeClass);
    }

    /**
     * Create a new, empty module in the global context.<br>
     * This is equivalent to calling LLVMModuleCreateWithNameInContext with<br>
     * LLVMGetGlobalContext() as the context parameter.<br>
     * Every invocation should be paired with LLVMDisposeModule() or memory<br>
     * will be leaked.
     */
    public static Module createWithName(String moduleID) {
        return new Module(LLVMLibrary.LLVMModuleCreateWithName(moduleID));
    }

    /**
     * Create a new, empty module in a specific context.<br>
     * Every invocation should be paired with LLVMDisposeModule() or memory<br>
     * will be leaked.
     */
    public static Module createWithNameInContext(String moduleID, Context c) {
        return new Module(LLVMLibrary.LLVMModuleCreateWithNameInContext(moduleID, c.context()));
    }

    @Override
    public void finalize() {
        dispose();
    }

    /**
     * Destroy a module instance.<br>
     * This must be called for every created module or memory will be<br>
     * leaked.
     */
    public void dispose() {
        LLVMLibrary.LLVMDisposeModule(module);
        module = null;
    }

    /**
     * Obtain the data layout for a module.<br>
     *
     * @see Module::getDataLayout()
     */
    public String getDataLayout() {
        return LLVMLibrary.LLVMGetDataLayout(module);
    }

    /**
     * Obtain the target triple for a module.<br>
     *
     * @see Module::getTargetTriple()
     */
    public String getTarget() {
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

    /**
     * Obtain a Type from a module by its registered name.
     */
    public TypeRef getTypeByName(String name) {
        return new TypeRef(LLVMLibrary.LLVMGetTypeByName(module, name));
    }

    /*public String getTypeName(LLVMTypeRef ty) {
        Pointer<Byte> cstr = LLVMGetTypeName(module, ty);
        return cstr.getCString();
    }*/

    /**
     * Dump a representation of a module to stderr.<br>
     *
     * @see Module::dump()
     */
    public void dumpModule() {
        LLVMLibrary.LLVMDumpModule(module);
    }

    /**
     * Writes a module to the specified path. Returns 0 on success.
     */
    public int writeBitcodeToFile(String path) {
        return LLVMLibrary.LLVMWriteBitcodeToFile(module, path);
    }

    public Context getModuleContext() {
        return Context.getModuleContext(this);
    }

    public Value getNamedGlobal(String name) {
        return new Value(LLVMLibrary.LLVMGetNamedGlobal(getModule(), name));
    }

    public Value getFirstGlobal() {
        try {
            return new Value(LLVMLibrary.LLVMGetFirstGlobal(getModule()));
        } catch (java.lang.IllegalArgumentException e) {
            return null;
        }
    }

    public Value getLastGlobal() {
        try {
            return new Value(LLVMLibrary.LLVMGetLastGlobal(getModule()));
        } catch (java.lang.IllegalArgumentException e) {
            return null;
        }
    }

    public Value addAlias(TypeRef ty, Value aliasee, String name) {
        return new Value(LLVMLibrary.LLVMAddAlias(module, ty.type(), aliasee.value(), name));
    }

    /**
     * Obtain a Function value from a Module by its name.<br>
     * The returned value corresponds to a llvm::Function value.<br>
     *
     * @see llvm::Module::getFunction()
     */
    public Value getNamedFunction(String name) {
        return new Value(LLVMLibrary.LLVMGetNamedFunction(module, name));
    }

    /**
     * Obtain an iterator to the first Function in a Module.<br>
     *
     * @see llvm::Module::begin()
     */
    public Value getFirstFunction() {
        try {
            return new Value(LLVMLibrary.LLVMGetFirstFunction(module));
        } catch (java.lang.IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Obtain an iterator to the last Function in a Module.<br>
     *
     * @see llvm::Module::end()
     */
    public Value getLastFunction() {
        try {
            return new Value(LLVMLibrary.LLVMGetLastFunction(module));
        } catch (java.lang.IllegalArgumentException e) {
            return null;
        }
    }

    private class ModuleIterator implements Iterator<Value> {
        private Value current;
        private Value last;

        public ModuleIterator() {
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
                if (current.equals(last))
                    current = null;
                else
                    current = current.getNextFunction();

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
