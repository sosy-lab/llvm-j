package org.llvm;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

import java.util.ArrayList;
import java.util.List;

import static org.llvm.binding.LLVMLibrary.*;

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

    private LLVMTypeRef type;

    public LLVMTypeRef type() {
        return type;
    }

    TypeRef(LLVMTypeRef type) {
        this.type = type;
    }

    /**
     * Obtain the enumerated type of a Type instance.<br>
     *
     * @see llvm::Type:getTypeID()
     */
    public TypeKind getTypeKind() {
        final int typeInt = LLVMGetTypeKind(type);

        if (typeInt == LLVMTypeKind.LLVMHalfTypeKind) {
            return TypeKind.Half;
        } else if (typeInt == LLVMTypeKind.LLVMFloatTypeKind) {
            return TypeKind.Float;
        } else if (typeInt == LLVMTypeKind.LLVMDoubleTypeKind) {
            return TypeKind.Double;
        } else if (typeInt == LLVMTypeKind.LLVMX86_FP80TypeKind) {
            return TypeKind.X86_FP80;
        } else if (typeInt == LLVMTypeKind.LLVMFP128TypeKind) {
            return TypeKind.FP128;
        } else if (typeInt == LLVMTypeKind.LLVMPPC_FP128TypeKind) {
            return TypeKind.PPC_FP128;
        } else if (typeInt == LLVMTypeKind.LLVMLabelTypeKind) {
            return TypeKind.Label;
        } else if (typeInt == LLVMTypeKind.LLVMIntegerTypeKind) {
            return TypeKind.Integer;
        } else if (typeInt == LLVMTypeKind.LLVMFunctionTypeKind) {
            return TypeKind.Function;
        } else if (typeInt == LLVMTypeKind.LLVMStructTypeKind) {
            return TypeKind.Struct;
        } else if (typeInt == LLVMTypeKind.LLVMArrayTypeKind) {
            return TypeKind.Array;
        } else if (typeInt == LLVMTypeKind.LLVMPointerTypeKind) {
            return TypeKind.Pointer;
        } else if (typeInt == LLVMTypeKind.LLVMMetadataTypeKind) {
            return TypeKind.Metadata;
        } else if (typeInt == LLVMTypeKind.LLVMTokenTypeKind) {
            return TypeKind.Token;
        } else if (typeInt == LLVMTypeKind.LLVMVoidTypeKind) {
            return TypeKind.Void;
        } else if (typeInt == LLVMTypeKind.LLVMX86_MMXTypeKind) {
            return TypeKind.X86_MMX;
        } else if (typeInt == LLVMTypeKind.LLVMVectorTypeKind) {
            return TypeKind.Vector;
        } else {
            throw new AssertionError("Unhanlded type kind id " + typeInt);
        }
    }

    public void dump() {
        LLVMDumpType(type);
    }

    /**
     * Obtain the context to which this type instance is associated.<br>
     *
     * @see llvm::Type::getContext()
     */
    public Context getTypeContext() {
        return new Context(LLVMGetTypeContext(type));
    }

    public int getIntTypeWidth() {
        return LLVMGetIntTypeWidth(type);
    }

    /**
     * Returns whether a function type is variadic.
     */
    public boolean isFunctionVarArg() {
        LLVMBool b = LLVMIsFunctionVarArg(type);
        return Utils.llvmBoolToJavaBool(b);
    }

    /**
     * Obtain the Type this function Type returns.
     */
    public TypeRef getReturnType() {
        return new TypeRef(LLVMGetReturnType(type));
    }

    /**
     * Obtain the number of parameters this function accepts.
     */
    public int countParamTypes() {
        return LLVMCountParamTypes(type);
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
            int typeRefOffset = Native.getNativeSize(LLVMTypeRef.class);
            Memory arrayPointer = new Memory(paramCount * typeRefOffset);
            LLVMTypeRef typeRefArray = new LLVMTypeRef(arrayPointer);
            LLVMGetParamTypes(type, typeRefArray);

            Pointer[] paramRefs = new Pointer[paramCount];
            arrayPointer.read(typeRefOffset, paramRefs, 0, paramCount);
            for (int i = 0; i < paramCount; i++) {
                LLVMTypeRef paramRef = new LLVMTypeRef(paramRefs[i]);
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
        return LLVMCountStructElementTypes(type);
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
            int typeRefOffset = Native.getNativeSize(LLVMTypeRef.class);
            Memory arrayPointer = new Memory(memberCount * typeRefOffset);
            LLVMTypeRef typeRefArray = new LLVMTypeRef(arrayPointer);
            LLVMGetStructElementTypes(type, typeRefArray);

            Pointer[] memberRefs = new Pointer[memberCount];
            arrayPointer.read(typeRefOffset, memberRefs, 0, memberCount);

            for (int i = 0; i < memberCount; i++) {
                LLVMTypeRef memberRef = new LLVMTypeRef(memberRefs[i]);
                members.add(new TypeRef(memberRef));
            }
        }

        return members;
    }

    public boolean isStructNamed() {
        String name = LLVMGetStructName(type);
        return name != null;
    }

    public String getStructName() {
        if (isStructNamed()) {
            return LLVMGetStructName(type);
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
        LLVMBool b = LLVMIsPackedStruct(type);
        return Utils.llvmBoolToJavaBool(b);
    }

    /**
     * Determine whether a structure is opaque.<br>
     *
     * @see llvm::StructType::isOpaque()<br>
     */
    public boolean isOpaqueStruct() {
        LLVMBool b = LLVMIsOpaqueStruct(type);
        return Utils.llvmBoolToJavaBool(b);
    }

    /**
     * Obtain the type of elements within a sequential type.<br>
     * This works on array, vector, and pointer types.<br>
     *
     * @see llvm::SequentialType::getElementType()
     */
    public TypeRef getElementType() {
        return new TypeRef(LLVMGetElementType(type));
    }

    /**
     * Obtain the length of an array type.<br>
     * This only works on types that represent arrays.<br>
     *
     * @see llvm::ArrayType::getNumElements()
     */
    public int getArrayLength() {
        return LLVMGetArrayLength(type);
    }

    /**
     * Obtain the address space of a pointer type.<br>
     * This only works on types that represent pointers.<br>
     *
     * @see llvm::PointerType::getAddressSpace()
     */
    public int getPointerAddressSpace() {
        return LLVMGetPointerAddressSpace(type);
    }

    /**
     * Obtain the number of elements in a vector type.<br>
     * This only works on types that represent vectors.<br>
     *
     * @see llvm::VectorType::getNumElements()
     */
    public int getVectorSize() {
        return LLVMGetVectorSize(type);
    }

    public Value alignOf(TypeRef ty) {
        return new Value(LLVMAlignOf(type));
    }

    public Value sizeOf(TypeRef ty) {
        return new Value(LLVMSizeOf(type));
    }
}
