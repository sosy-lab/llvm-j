package org.llvm;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeMappedConverter;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import org.llvm.binding.LLVMLibrary.*;

import java.util.Iterator;

import static org.llvm.binding.LLVMLibrary.*;

/**
 * The main container class for the LLVM Intermediate Representation.
 */
public class Module implements Iterable<Value> {
    private LLVMModuleRef module;

    LLVMModuleRef getModule() {
        return module;
    }

    Module(LLVMModuleRef module) {
        this.module = module;
    }

    /**
     * Parse a module from file
     */
    public static Module parseIR(String path) {
        /* read the module into a buffer */
        //Pointer address = new Memory(1000*1000*8);
        //LLVMMemoryBufferRef buffer = new LLVMMemoryBufferRef(address);

        PointerByReference pointerToBuffer = new PointerByReference();
        LLVMMemoryBufferRef pointerToBufferWrapped = new LLVMMemoryBufferRef(pointerToBuffer.getPointer());
        //LLVMMemoryBufferRef buffer = LLVMCreateMemoryBufferWithMemoryRange(fileContent, new NativeSize(800 * 1000 * 1000), "test", no);
        Pointer outMsgAddr = new Memory(1000 * 1000 * 10 * 8);
        PointerByReference outMsg = new PointerByReference(outMsgAddr);
        LLVMBool success = LLVMCreateMemoryBufferWithContentsOfFile(path, pointerToBufferWrapped, outMsg);
        LLVMMemoryBufferRef buffer = new LLVMMemoryBufferRef(pointerToBuffer.getValue());
        // if (!Utils.llvmBoolToJavaBool(success)) {
        //    System.err.println("Reading bitcode failed\n");
        //    return null;
        // }

    /* create a module from the memory buffer */
        PointerByReference pointerToModule = new PointerByReference(new Memory(getSize(LLVMModuleRef.class)));
        LLVMModuleRef pointerToModuleWrapped = new LLVMModuleRef(pointerToModule.getPointer());
        success = LLVMParseBitcode2(buffer, pointerToModuleWrapped);
        //if (!Utils.llvmBoolToJavaBool(success)) {
        //    return null;
        //}
        LLVMModuleRef module = new LLVMModuleRef(pointerToModule.getValue());
        /* free the buffer allocated by readFileToBuffer */
        LLVMDisposeMemoryBuffer(buffer);

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
        return new Module(LLVMModuleCreateWithName(moduleID));
    }

    /**
     * Create a new, empty module in a specific context.<br>
     * Every invocation should be paired with LLVMDisposeModule() or memory<br>
     * will be leaked.
     */
    public static Module createWithNameInContext(String moduleID, Context c) {
        return new Module(LLVMModuleCreateWithNameInContext(moduleID, c.context()));
    }

    public void finalize() {
        dispose();
    }

    /**
     * Destroy a module instance.<br>
     * This must be called for every created module or memory will be<br>
     * leaked.
     */
    public void dispose() {
        LLVMDisposeModule(module);
        module = null;
    }

    /**
     * Obtain the data layout for a module.<br>
     *
     * @see Module::getDataLayout()
     */
    public String getDataLayout() {
        return LLVMGetDataLayout(module);
    }

    /**
     * Obtain the target triple for a module.<br>
     *
     * @see Module::getTargetTriple()
     */
    public String getTarget() {
        return LLVMGetTarget(module);
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
        return new TypeRef(LLVMGetTypeByName(module, name));
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
        LLVMDumpModule(module);
    }

    /**
     * Writes a module to the specified path. Returns 0 on success.
     */
    public int writeBitcodeToFile(String path) {
        return LLVMWriteBitcodeToFile(module, path);
    }

    public Context getModuleContext() {
        return Context.getModuleContext(this);
    }

    public Value getNamedGlobal(String name) {
        return new Value(LLVMGetNamedGlobal(getModule(), name));
    }

    public Value getFirstGlobal() {
        try {
            return new Value(LLVMGetFirstGlobal(getModule()));
        } catch (java.lang.IllegalArgumentException e) {
            return null;
        }
    }

    public Value getLastGlobal() {
        try {
            return new Value(LLVMGetLastGlobal(getModule()));
        } catch (java.lang.IllegalArgumentException e) {
            return null;
        }
    }

    public Value addAlias(TypeRef ty, Value aliasee, String name) {
        return new Value(LLVMAddAlias(module, ty.type(), aliasee.value(), name));
    }

    /**
     * Obtain a Function value from a Module by its name.<br>
     * The returned value corresponds to a llvm::Function value.<br>
     *
     * @see llvm::Module::getFunction()
     */
    public Value getNamedFunction(String name) {
        return new Value(LLVMGetNamedFunction(module, name));
    }

    /**
     * Obtain an iterator to the first Function in a Module.<br>
     *
     * @see llvm::Module::begin()
     */
    public Value getFirstFunction() {
        try {
            return new Value(LLVMGetFirstFunction(module));
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
            return new Value(LLVMGetLastFunction(module));
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

        public boolean hasNext() {
            return current != null;
        }

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

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public Iterator<Value> iterator() {
        return new ModuleIterator();
    }

}
