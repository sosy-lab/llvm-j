package org.sosy_lab.llvm_j;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeMappedConverter;
import com.sun.jna.Pointer;
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
            NativeLibrary.addSearchPath(
                    LLVMLibrary.JNA_LIBRARY_NAME, p.toAbsolutePath().toString());
        }
    }

    /**
     * Parses a module from the given file.
     */
    public static Module parseIR(String path) throws LLVMException {
        LLVMLibrary.instantiate();
        /* read the module into a buffer */

        PointerByReference pointerToBuffer = new PointerByReference();
        LLVMLibrary.LLVMMemoryBufferRef pointerToBufferWrapped =
                new LLVMLibrary.LLVMMemoryBufferRef(pointerToBuffer.getPointer());
        Pointer outMsgAddr = new Memory(1000 * 1000 * 10 * 8);
        PointerByReference outMsg = new PointerByReference(outMsgAddr);
        LLVMLibrary.LLVMBool success = LLVMLibrary.LLVMCreateMemoryBufferWithContentsOfFile(
                path,
                pointerToBufferWrapped,
                outMsg);
        if (!Utils.llvmBoolToJavaBool(success)) {
            throw new LLVMException("Reading bitcode failed");
        }
        LLVMLibrary.LLVMMemoryBufferRef buffer =
                new LLVMLibrary.LLVMMemoryBufferRef(pointerToBuffer.getValue());

        /* create a module from the memory buffer */
        long moduleRefSize = getSize(LLVMLibrary.LLVMModuleRef.class);
        PointerByReference pointerToModule = new PointerByReference(new Memory(moduleRefSize));
        LLVMLibrary.LLVMModuleRef pointerToModuleWrapped =
                new LLVMLibrary.LLVMModuleRef(pointerToModule.getPointer());
        success = LLVMLibrary.LLVMParseBitcode2(buffer, pointerToModuleWrapped);
        if (!Utils.llvmBoolToJavaBool(success)) {
            throw new LLVMException("Parsing bitcode failed");
        }
        LLVMLibrary.LLVMModuleRef module =
                new LLVMLibrary.LLVMModuleRef(pointerToModule.getValue());

        /* free the buffer allocated by readFileToBuffer */
        LLVMLibrary.LLVMDisposeMemoryBuffer(buffer);

        return new Module(module);
    }

    private static long getSize(Class<?> pClass) {
        Class<?> nativeClass = NativeMappedConverter.getInstance(pClass).nativeType();
        return Native.getNativeSize(nativeClass);
    }

    /**
     * Creates a new, empty module in the global context.<br>
     * Every invocation should be paired with {link #dispose()} or memory
     * will be leaked.
     *
     * @param moduleID the name of the new module
     */
    public static Module createWithName(String moduleID) {
        return new Module(LLVMLibrary.LLVMModuleCreateWithName(moduleID));
    }

    /**
     * Creates a new, empty module in a specific context.<br>
     * Every invocation should be paired with {@link #dispose()} or memory
     * will be leaked.
     *
     * @param moduleID the name of the new module
     * @param c the context to create the new module in
     */
    public static Module createWithNameInContext(String moduleID, Context c) {
        return new Module(LLVMLibrary.LLVMModuleCreateWithNameInContext(moduleID, c.context()));
    }

    @Override
    protected void finalize() {
        dispose();
    }

    /**
     * Destroys this module instance.<br>
     * This must be called for every created module or memory will be
     * leaked.
     */
    public void dispose() {
        LLVMLibrary.LLVMDisposeModule(module);
        module = null;
    }

    /**
     * Returns the data layout for this module.
     */
    public String getDataLayout() {
        return LLVMLibrary.LLVMGetDataLayout(module);
    }

    /**
     * Returns the target triple for this module.
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
     * Returns a {@link TypeRef} from this module by its registered name.
     */
    public TypeRef getTypeByName(String name) {
        return new TypeRef(LLVMLibrary.LLVMGetTypeByName(module, name));
    }

    /*public String getTypeName(LLVMTypeRef ty) {
        Pointer<Byte> cstr = LLVMGetTypeName(module, ty);
        return cstr.getCString();
    }*/

    /**
     * Dumps a representation of this module to stderr.
     */
    public void dumpModule() {
        LLVMLibrary.LLVMDumpModule(module);
    }

    /**
     * Writes this module to the specified path.
     *
     * @return returns 0 on success, an error code otherwise.
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
     * Returns a {@link Function} from this module by its name.
     */
    public Function getNamedFunction(String name) {
        return new Function(LLVMLibrary.LLVMGetNamedFunction(module, name));
    }

    /**
     * Returns an iterator to the first Function in this module.
     */
    public Value getFirstFunction() {
        try {
            return new Value(LLVMLibrary.LLVMGetFirstFunction(module));
        } catch (java.lang.IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Returns an iterator to the last Function in this module.
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