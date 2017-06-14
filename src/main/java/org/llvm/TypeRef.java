package org.llvm;

import org.bridj.IntValuedEnum;
import org.bridj.Pointer;
import org.llvm.binding.LLVMLibrary.LLVMTypeKind;
import org.llvm.binding.LLVMLibrary.LLVMTypeRef;

import java.util.ArrayList;
import java.util.List;

import static org.llvm.binding.LLVMLibrary.*;

/**
 * Each value in the LLVM IR has a type, an LLVMTypeRef.
 */
public class TypeRef {

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
    public IntValuedEnum<LLVMTypeKind> getTypeKind() {
        return LLVMGetTypeKind(type);
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

    /**
     * Obtain an integer type from a context with specified bit width.
     */
    public static TypeRef int1TypeInContext(Context c) {
        return new TypeRef(LLVMInt1TypeInContext(c.context()));
    }

    public static TypeRef int8TypeInContext(Context c) {
        return new TypeRef(LLVMInt8TypeInContext(c.context()));
    }

    public static TypeRef int16TypeInContext(Context c) {
        return new TypeRef(LLVMInt16TypeInContext(c.context()));
    }

    public static TypeRef int32TypeInContext(Context c) {
        return new TypeRef(LLVMInt32TypeInContext(c.context()));
    }

    public static TypeRef int64TypeInContext(Context c) {
        return new TypeRef(LLVMInt64TypeInContext(c.context()));
    }

    public static TypeRef intTypeInContext(Context c, int NumBits) {
        return new TypeRef(LLVMIntTypeInContext(c.context(), NumBits));
    }

    /**
     * Obtain an integer type from the global context with a specified bit<br>
     * width.
     */
    public static TypeRef int1Type() {
        return new TypeRef(LLVMInt1Type());
    }

    public static TypeRef int8Type() {
        return new TypeRef(LLVMInt8Type());
    }

    public static TypeRef int16Type() {
        return new TypeRef(LLVMInt16Type());
    }

    public static TypeRef int32Type() {
        return new TypeRef(LLVMInt32Type());
    }

    public static TypeRef int64Type() {
        return new TypeRef(LLVMInt64Type());
    }

    public static TypeRef intType(int NumBits) {
        return new TypeRef(LLVMIntType(NumBits));
    }

    public int getIntTypeWidth() {
        return LLVMGetIntTypeWidth(type);
    }

    /**
     * Obtain a 32-bit floating point type from a context.
     */
    public static TypeRef floatTypeInContext(Context c) {
        return new TypeRef(LLVMFloatTypeInContext(c.context()));
    }

    /**
     * Obtain a 64-bit floating point type from a context.
     */
    public static TypeRef doubleTypeInContext(Context c) {
        return new TypeRef(LLVMDoubleTypeInContext(c.context()));
    }

    /**
     * Obtain a 80-bit floating point type (X87) from a context.
     */
    public static TypeRef x86FP80TypeInContext(Context c) {
        return new TypeRef(LLVMX86FP80TypeInContext(c.context()));
    }

    /**
     * Obtain a 128-bit floating point type (112-bit mantissa) from a<br>
     * context.
     */
    public static TypeRef FP128TypeInContext(Context c) {
        return new TypeRef(LLVMFP128TypeInContext(c.context()));
    }

    /**
     * Obtain a 128-bit floating point type (two 64-bits) from a context.
     */
    public static TypeRef PPCFP128TypeInContext(Context c) {
        return new TypeRef(LLVMPPCFP128TypeInContext(c.context()));
    }

    public static TypeRef floatType() {
        return new TypeRef(LLVMFloatType());
    }

    public static TypeRef doubleType() {
        return new TypeRef(LLVMDoubleType());
    }

    public static TypeRef x86FP80Type() {
        return new TypeRef(LLVMX86FP80Type());
    }

    public static TypeRef FP128Type() {
        return new TypeRef(LLVMFP128Type());
    }

    public static TypeRef PPCFP128Type() {
        return new TypeRef(LLVMPPCFP128Type());
    }

    /**
     * Obtain a function type consisting of a specified signature.<br>
     * The function is defined as a tuple of a return Type, a list of<br>
     * parameter types, and whether the function is variadic.
     */
    public static TypeRef functionType(LLVMTypeRef returnType,
            Pointer<LLVMTypeRef> paramTypes, int paramCount, int isVarArg) {
        return new TypeRef(LLVMFunctionType(returnType, paramTypes, paramCount,
                isVarArg));
    }

    public static TypeRef functionType(TypeRef returnType,
            TypeRef... paramTypes) {
        return new TypeRef(LLVMFunctionType(returnType.type,
                internalize(paramTypes), paramTypes.length, 0));
    }

    public static TypeRef functionType(TypeRef returnType, boolean isVarArg,
            TypeRef... paramTypes) {
        return new TypeRef(LLVMFunctionType(returnType.type,
                internalize(paramTypes), paramTypes.length, isVarArg ? 1 : 0));
    }

    /**
     * Returns whether a function type is variadic.
     */
    public boolean isFunctionVarArg() {
        return LLVMIsFunctionVarArg(type) != 0;
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
        Pointer<LLVMTypeRef> paramRef = Pointer.allocateArray(LLVMTypeRef.class, paramCount);
        LLVMGetParamTypes(type, paramRef);

        List<TypeRef> params = new ArrayList<TypeRef>(paramCount);
        for (int i = 0; i < paramCount; i++) {
            params.add(new TypeRef(paramRef.get(i)));
        }

        return params;
    }

    /**
     * Create a new structure type in a context.<br>
     * A structure is specified by a list of inner elements/types and<br>
     * whether these can be packed together.<br>
     *
     * @see llvm::StructType::create()
     */
    public static TypeRef structTypeInContext(Context c,
            Pointer<LLVMTypeRef> elementTypes, int elementCount, boolean packed) {
        return new TypeRef(LLVMStructTypeInContext(c.context(), elementTypes,
                elementCount, packed ? 1 : 0));
    }

    /**
     * Create a new structure type in the global context.<br>
     *
     * @see llvm::StructType::create()
     */
    public static TypeRef structType(Pointer<LLVMTypeRef> elementTypes,
            int elementCount, boolean packed) {
        return new TypeRef(LLVMStructType(elementTypes, elementCount,
                packed ? 1 : 0));
    }

    /**
     * Create a new non-packed structure type in the global context.
     */
    public static TypeRef structType(TypeRef... elementTypes) {
        return new TypeRef(LLVMStructType(internalize(elementTypes),
                elementTypes.length, 0));
    }

    /**
     * Create an empty identified structure in the global context.
     */
    public static TypeRef structTypeNamed(String name) {
        return new TypeRef(LLVMStructCreateNamed(LLVMGetGlobalContext(),
                Pointer.pointerToCString(name == null ? "" : name)));
    }

    /**
     * Create an empty identified structure in a context.
     */
    public static TypeRef structTypeNamed(Context c, String name) {
        return new TypeRef(LLVMStructCreateNamed(c.context(),
                Pointer.pointerToCString(name == null ? "" : name)));
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
        int memberCount = countStructElementTypes();
        Pointer<LLVMTypeRef> memberRef = Pointer.allocateArray(LLVMTypeRef.class, memberCount);
        LLVMGetStructElementTypes(type, memberRef);

        List<TypeRef> members = new ArrayList<TypeRef>(memberCount);
        for (int i = 0; i < memberCount; i++) {
            members.add(new TypeRef(memberRef.get(i)));
        }

        return members;
    }

    public boolean isStructNamed() {
        Pointer<Byte> name = LLVMGetStructName(type);
        return name.getPeer() != 0;
    }

    public String getStructName() {
        Pointer<Byte> name = LLVMGetStructName(type);
        if (name.getPeer() != 0) {
            return name.getString(Pointer.StringType.C);
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
        return LLVMIsPackedStruct(type) != 0;
    }

    /**
     * Determine whether a structure is opaque.<br>
     *
     * @see llvm::StructType::isOpaque()<br>
     */
    public boolean isOpaqueStruct() {
        return LLVMIsOpaqueStruct(type) != 0;
    }

    /**
     * Create a fixed size array type that refers to a specific type.<br>
     * The created type will exist in the context that its element type<br>
     * exists in.<br>
     *
     * @see llvm::ArrayType::get()
     */
    public TypeRef arrayType(int elementCount) {
        return new TypeRef(LLVMArrayType(type, elementCount));
    }

    /**
     * Create a pointer type that points to a defined type.<br>
     * The created type will exist in the context that its pointee type<br>
     * exists in.<br>
     *
     * @see llvm::PointerType::get()
     */
    public TypeRef pointerType(int addressSpace) {
        return new TypeRef(LLVMPointerType(type, addressSpace));
    }

    /**
     * Create a pointer type that points to a defined type.<br>
     * The created type will exist in the context that its pointee type<br>
     * exists in and the default address space (0).<br>
     *
     * @see llvm::PointerType::get()
     */
    public TypeRef pointerType() {
        return new TypeRef(LLVMPointerType(type, 0));
    }

    /**
     * Create a vector type that contains a defined type and has a specific<br>
     * number of elements.<br>
     * The created type will exist in the context thats its element type<br>
     * exists in.<br>
     *
     * @see llvm::VectorType::get()
     */
    public TypeRef vectorType(int elementCount) {
        return new TypeRef(LLVMVectorType(type, elementCount));
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

    /**
     * Create a void type in a context.
     */
    public static TypeRef voidTypeInContext(Context c) {
        return new TypeRef(LLVMVoidTypeInContext(c.context()));
    }

    /**
     * Create a label type in a context.
     */
    public static TypeRef labelTypeInContext(Context c) {
        return new TypeRef(LLVMLabelTypeInContext(c.context()));
    }

    public static TypeRef opaqueTypeInContext(Context c) {
        return structTypeNamed(c, null);
    }

    /**
     * Create a X86 MMX type in a context.
     */
    public static TypeRef x86MMXTypeInContext(Context c) {
        return new TypeRef(LLVMX86MMXTypeInContext(c.context()));
    }

    /**
     * These are similar to the above functions except they operate on the<br>
     * global context.
     */
    public static TypeRef voidType() {
        return new TypeRef(LLVMVoidType());
    }

    public static TypeRef labelType() {
        return new TypeRef(LLVMLabelType());
    }

    public static TypeRef opaqueType() {
        return structTypeNamed(null);
    }

    public static TypeRef x86MMXType() {
        return new TypeRef(LLVMX86MMXType());
    }

    //public static TypeHandleRef createTypeHandle(LLVMTypeRef PotentiallyAbstractTy);
    //public static void    refineType(LLVMTypeRef AbstractTy, LLVMTypeRef ConcreteTy);
    //public static TypeRef resolveTypeHandle(LLVMTypeHandleRef TypeHandle);
    //public static void    disposeTypeHandle(LLVMTypeHandleRef TypeHandle);

    /**
     * Obtain a constant value referring to the null instance of a type.<br>
     *
     * @see llvm::Constant::getNullValue()
     */
    public Value constNull() {
        return new Value(LLVMConstNull(type));
    }

    /**
     * Obtain a constant that is a constant pointer pointing to NULL for a<br>
     * specified type.
     */
    public Value constPointerNull() {
        return new Value(LLVMConstPointerNull(type));
    }

    /**
     * Obtain a constant value referring to the instance of a type<br>
     * consisting of all ones.<br>
     * This is only valid for integer types.<br>
     * * @see llvm::Constant::getAllOnesValue()
     */
    public Value constAllOnes() {
        return new Value(LLVMConstAllOnes(type));
    }

    /**
     * Obtain a constant value referring to an undefined value of a type.<br>
     *
     * @see llvm::UndefValue::get()
     */
    public Value getUndef() {
        return new Value(LLVMGetUndef(type));
    }

    /**
     * Obtain a constant value for an integer type.<br>
     * The returned value corresponds to a llvm::ConstantInt.<br>
     *
     * @see llvm::ConstantInt::get()<br>
     * @param n
     *            The value the returned instance should refer to.<br>
     * @param signExtend
     *            Whether to sign extend the produced value.
     */
    public Value constInt(long n, boolean signExtend) {
        return new Value(LLVMConstInt(type, n, signExtend ? 1 : 0));
    }

    /**
     * Obtain a constant value for an integer of arbitrary precision.<br>
     *
     * @see llvm::ConstantInt::get()
     */
    // TODO: change Pointer to array
    public Value constIntOfArbitraryPrecision(int numWords, Pointer<Long> words) {
        return new Value(
                LLVMConstIntOfArbitraryPrecision(type, numWords, words));
    }

    /**
     * Obtain a constant value for an integer parsed from a string.<br>
     * A similar API, LLVMConstIntOfStringAndSize is also available. If the<br>
     * string's length is available, it is preferred to call that function<br>
     * instead.<br>
     *
     * @see llvm::ConstantInt::get()
     */
    public Value constIntOfString(String text, byte radix) {
        return new Value(LLVMConstIntOfString(type,
                Pointer.pointerToCString(text), radix));
    }

    /**
     * Obtain a constant value for an integer parsed from a string with<br>
     * specified length.<br>
     *
     * @see llvm::ConstantInt::get()
     */
    public Value constIntOfStringAndSize(String text, int sLen, byte radix) {
        return new Value(LLVMConstIntOfStringAndSize(type,
                Pointer.pointerToCString(text), sLen, radix));
    }

    /**
     * Obtain a constant value referring to a double floating point value.
     */
    public Value constReal(double n) {
        return new Value(LLVMConstReal(type, n));
    }

    /**
     * Obtain a constant for a floating point value parsed from a string.<br>
     * A similar API, LLVMConstRealOfStringAndSize is also available. It<br>
     * should be used if the input string's length is known.
     */
    public Value constRealOfString(String text) {
        return new Value(LLVMConstRealOfString(type,
                Pointer.pointerToCString(text)));
    }

    /**
     * Obtain a constant for a floating point value parsed from a string.
     */
    public Value constRealOfStringAndSize(String text, int sLen) {
        return new Value(LLVMConstRealOfStringAndSize(type,
                Pointer.pointerToCString(text), sLen));
    }

    public Value alignOf(TypeRef ty) {
        return new Value(LLVMAlignOf(type));
    }

    public Value sizeOf(TypeRef ty) {
        return new Value(LLVMSizeOf(type));
    }

    static Pointer<LLVMTypeRef> internalize(TypeRef[] types) {
        int n = types.length;
        LLVMTypeRef[] inner = new LLVMTypeRef[n];
        for (int i = 0; i < n; i++) {
            inner[i] = types[i].type;
        }

        Pointer<LLVMTypeRef> array = Pointer.allocateTypedPointers(
                LLVMTypeRef.class, types.length);
        array.setArray(inner);

        return array;
    }

}
