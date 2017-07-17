package org.sosy_lab.llvm_j;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.sosy_lab.llvm_j.binding.LLVMLibrary;

import java.util.ArrayList;
import java.util.List;

/**
 * Each value in the LLVM IR has a type, an LLVMTypeRef.
 */
public class TypeRef {

    public enum TypeKind {
        Void,
        /** < 16 bit floating point type */
        Half,
        /** < 32 bit floating point type */
        Float,
        /** < 64 bit floating point type */
        Double,
        /** < 80 bit floating point type (X87) */
        X86_FP80,
        /** < 128 bit floating point type (112-bit mantissa) */
        FP128,
        /** < 128 bit floating point type (two 64-bits) */
        PPC_FP128,
        /** < Labels */
        Label,
        /** < Arbitrary bit width integers */
        Integer,
        /** < Functions */
        Function,
        /** < Structures */
        Struct,
        /** < Arrays */
        Array,
        /** < Pointers */
        Pointer,
        /** < SIMD 'packed' format, or other vector type */
        Vector,
        /** < Metadata */
        Metadata,
        /** < X86 MMX */
        X86_MMX,
        /** < Tokens */
        Token,
    }

    private LLVMLibrary.LLVMTypeRef type;

    public LLVMLibrary.LLVMTypeRef type() {
        return type;
    }

    TypeRef(LLVMLibrary.LLVMTypeRef type) {
        this.type = type;
    }

    /**
     * Obtain the enumerated type of a Type instance.<br>
     *
     * @see llvm::Type:getTypeID()
     */
    public TypeKind getTypeKind() {
        final int typeInt = LLVMLibrary.LLVMGetTypeKind(type);

        if (typeInt == LLVMLibrary.LLVMTypeKind.LLVMHalfTypeKind) {
            return TypeKind.Half;
        } else if (typeInt == LLVMLibrary.LLVMTypeKind.LLVMFloatTypeKind) {
            return TypeKind.Float;
        } else if (typeInt == LLVMLibrary.LLVMTypeKind.LLVMDoubleTypeKind) {
            return TypeKind.Double;
        } else if (typeInt == LLVMLibrary.LLVMTypeKind.LLVMX86_FP80TypeKind) {
            return TypeKind.X86_FP80;
        } else if (typeInt == LLVMLibrary.LLVMTypeKind.LLVMFP128TypeKind) {
            return TypeKind.FP128;
        } else if (typeInt == LLVMLibrary.LLVMTypeKind.LLVMPPC_FP128TypeKind) {
            return TypeKind.PPC_FP128;
        } else if (typeInt == LLVMLibrary.LLVMTypeKind.LLVMLabelTypeKind) {
            return TypeKind.Label;
        } else if (typeInt == LLVMLibrary.LLVMTypeKind.LLVMIntegerTypeKind) {
            return TypeKind.Integer;
        } else if (typeInt == LLVMLibrary.LLVMTypeKind.LLVMFunctionTypeKind) {
            return TypeKind.Function;
        } else if (typeInt == LLVMLibrary.LLVMTypeKind.LLVMStructTypeKind) {
            return TypeKind.Struct;
        } else if (typeInt == LLVMLibrary.LLVMTypeKind.LLVMArrayTypeKind) {
            return TypeKind.Array;
        } else if (typeInt == LLVMLibrary.LLVMTypeKind.LLVMPointerTypeKind) {
            return TypeKind.Pointer;
        } else if (typeInt == LLVMLibrary.LLVMTypeKind.LLVMMetadataTypeKind) {
            return TypeKind.Metadata;
        } else if (typeInt == LLVMLibrary.LLVMTypeKind.LLVMTokenTypeKind) {
            return TypeKind.Token;
        } else if (typeInt == LLVMLibrary.LLVMTypeKind.LLVMVoidTypeKind) {
            return TypeKind.Void;
        } else if (typeInt == LLVMLibrary.LLVMTypeKind.LLVMX86_MMXTypeKind) {
            return TypeKind.X86_MMX;
        } else if (typeInt == LLVMLibrary.LLVMTypeKind.LLVMVectorTypeKind) {
            return TypeKind.Vector;
        } else {
            throw new AssertionError("Unhanlded type kind id " + typeInt);
        }
    }

    public void dump() {
        LLVMLibrary.LLVMDumpType(type);
    }

    /**
     * Obtain the context to which this type instance is associated.<br>
     *
     * @see llvm::Type::getContext()
     */
    public Context getTypeContext() {
        return new Context(LLVMLibrary.LLVMGetTypeContext(type));
    }

    public int getIntTypeWidth() {
        return LLVMLibrary.LLVMGetIntTypeWidth(type);
    }

    /**
     * Returns whether a function type is variadic.
     */
    public boolean isFunctionVarArg() {
        LLVMLibrary.LLVMBool b = LLVMLibrary.LLVMIsFunctionVarArg(type);
        return Utils.llvmBoolToJavaBool(b);
    }

    /**
     * Obtain the Type this function Type returns.
     */
    public TypeRef getReturnType() {
        return new TypeRef(LLVMLibrary.LLVMGetReturnType(type));
    }

    /**
     * Obtain the number of parameters this function accepts.
     */
    public int countParamTypes() {
        return LLVMLibrary.LLVMCountParamTypes(type);
    }

    /**
     * Obtain the types of a function's parameters.<br>
     * The Dest parameter should point to a pre-allocated array of<br>
     * LLVMTypeRef at least LLVMCountParamTypes() large. On return, the<br>
     * first LLVMCountParamTypes() entries in the array will be populated<br>
     * with LLVMTypeRef instances.<br>
     *
     */
    public List<TypeRef> getParamTypes() {
        int paramCount = countParamTypes();
        List<TypeRef> params = new ArrayList<TypeRef>(paramCount);

        if (paramCount > 0) {
            int typeRefOffset = Native.getNativeSize(LLVMLibrary.LLVMTypeRef.class);
            Memory arrayPointer = new Memory(paramCount * typeRefOffset);
            LLVMLibrary.LLVMTypeRef typeRefArray = new LLVMLibrary.LLVMTypeRef(arrayPointer);
            LLVMLibrary.LLVMGetParamTypes(type, typeRefArray);

            Pointer[] paramRefs = new Pointer[paramCount];
            arrayPointer.read(typeRefOffset, paramRefs, 0, paramCount);
            for (int i = 0; i < paramCount; i++) {
                LLVMLibrary.LLVMTypeRef paramRef = new LLVMLibrary.LLVMTypeRef(paramRefs[i]);
                params.add(new TypeRef(paramRef));
            }
        }

        return params;
    }

    /**
     * Get the number of elements defined inside the structure.<br>
     *
     * @see llvm::StructType::getNumElements()
     */
    public int countStructElementTypes() {
        return LLVMLibrary.LLVMCountStructElementTypes(type);
    }

    /**
     * Get the elements within a structure.<br>
     * The function is passed the address of a pre-allocated array of<br>
     * LLVMTypeRef at least LLVMCountStructElementTypes() long. After<br>
     * invocation, this array will be populated with the structure's<br>
     * elements. The objects in the destination array will have a lifetime<br>
     * of the structure type itself, which is the lifetime of the context it<br>
     * is contained in.
     */
    public List<TypeRef> getStructElementTypes() {
        int memberCount = countParamTypes();
        List<TypeRef> members = new ArrayList<TypeRef>(memberCount);

        if (memberCount > 0) {
            int typeRefOffset = Native.getNativeSize(LLVMLibrary.LLVMTypeRef.class);
            Memory arrayPointer = new Memory(memberCount * typeRefOffset);
            LLVMLibrary.LLVMTypeRef typeRefArray = new LLVMLibrary.LLVMTypeRef(arrayPointer);
            LLVMLibrary.LLVMGetStructElementTypes(type, typeRefArray);

            Pointer[] memberRefs = new Pointer[memberCount];
            arrayPointer.read(typeRefOffset, memberRefs, 0, memberCount);

            for (int i = 0; i < memberCount; i++) {
                LLVMLibrary.LLVMTypeRef memberRef = new LLVMLibrary.LLVMTypeRef(memberRefs[i]);
                members.add(new TypeRef(memberRef));
            }
        }

        return members;
    }

    public boolean isStructNamed() {
        String name = LLVMLibrary.LLVMGetStructName(type);
        return name != null;
    }

    public String getStructName() {
        if (isStructNamed()) {
            return LLVMLibrary.LLVMGetStructName(type);
        } else {
            throw new IllegalStateException("Type is not named struct");
        }
    }


    /**
     * Determine whether a structure is packed.<br>
     *
     * @see llvm::StructType::isPacked()
     */
    public boolean isPackedStruct() {
        LLVMLibrary.LLVMBool b = LLVMLibrary.LLVMIsPackedStruct(type);
        return Utils.llvmBoolToJavaBool(b);
    }

    /**
     * Determine whether a structure is opaque.<br>
     *
     * @see llvm::StructType::isOpaque()<br>
     */
    public boolean isOpaqueStruct() {
        LLVMLibrary.LLVMBool b = LLVMLibrary.LLVMIsOpaqueStruct(type);
        return Utils.llvmBoolToJavaBool(b);
    }

    /**
     * Obtain the type of elements within a sequential type.<br>
     * This works on array, vector, and pointer types.<br>
     *
     * @see llvm::SequentialType::getElementType()
     */
    public TypeRef getElementType() {
        return new TypeRef(LLVMLibrary.LLVMGetElementType(type));
    }

    /**
     * Obtain the length of an array type.<br>
     * This only works on types that represent arrays.<br>
     *
     * @see llvm::ArrayType::getNumElements()
     */
    public int getArrayLength() {
        return LLVMLibrary.LLVMGetArrayLength(type);
    }

    /**
     * Obtain the address space of a pointer type.<br>
     * This only works on types that represent pointers.<br>
     *
     * @see llvm::PointerType::getAddressSpace()
     */
    public int getPointerAddressSpace() {
        return LLVMLibrary.LLVMGetPointerAddressSpace(type);
    }

    /**
     * Obtain the number of elements in a vector type.<br>
     * This only works on types that represent vectors.<br>
     *
     * @see llvm::VectorType::getNumElements()
     */
    public int getVectorSize() {
        return LLVMLibrary.LLVMGetVectorSize(type);
    }

    public Value alignOf(TypeRef ty) {
        return new Value(LLVMLibrary.LLVMAlignOf(type));
    }

    public Value sizeOf(TypeRef ty) {
        return new Value(LLVMLibrary.LLVMSizeOf(type));
    }
}
