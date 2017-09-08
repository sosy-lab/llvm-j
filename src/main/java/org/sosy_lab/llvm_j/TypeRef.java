package org.sosy_lab.llvm_j;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import java.util.ArrayList;
import java.util.List;
import org.sosy_lab.llvm_j.binding.LLVMLibrary;

/** Each value in the LLVM IR has a type, an LLVMTypeRef. */
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

  /** Returns the enumerated type of this type instance. */
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

  public long offsetOfElement(LLVMLibrary.LLVMTargetDataRef TD, int idx) {
      assert getTypeKind() == TypeKind.Struct;
      return LLVMLibrary.LLVMOffsetOfElement(TD, type, idx);
  }

  public long storeSize(LLVMLibrary.LLVMTargetDataRef TD) {
      return LLVMLibrary.LLVMStoreSizeOfType(TD, type);
  }

  public void dump() {
    LLVMLibrary.LLVMDumpType(type);
  }

  /** Returns the context with which this type instance is associated. */
  public Context getTypeContext() {
    return new Context(LLVMLibrary.LLVMGetTypeContext(type));
  }

  public int getIntTypeWidth() {
    return LLVMLibrary.LLVMGetIntTypeWidth(type);
  }

  /** Returns whether this is a variadic function type. */
  public boolean isFunctionVarArg() {
    LLVMLibrary.LLVMBool b = LLVMLibrary.LLVMIsFunctionVarArg(type);
    return Utils.llvmBoolToJavaBool(b);
  }

  /** Returns the type this function type returns. Only works if this is a function type. */
  public TypeRef getReturnType() {
    return new TypeRef(LLVMLibrary.LLVMGetReturnType(type));
  }

  /**
   * Returns the number of parameters this function type accepts. Only works if this is a function
   * type.
   */
  public int countParamTypes() {
    return LLVMLibrary.LLVMCountParamTypes(type);
  }

  /** Returns the types of a function's parameters. Only works if this is a function type. */
  public List<TypeRef> getParamTypes() {
    int paramCount = countParamTypes();
    List<TypeRef> params = new ArrayList<>(paramCount);

    if (paramCount > 0) {
      int typeRefSize = Native.getNativeSize(LLVMLibrary.LLVMTypeRef.class);
      Memory arrayPointer = new Memory(paramCount * typeRefSize);
      LLVMLibrary.LLVMTypeRef typeRefArray = new LLVMLibrary.LLVMTypeRef(arrayPointer);
      LLVMLibrary.LLVMGetParamTypes(type, typeRefArray);

      Pointer[] paramRefs = new Pointer[paramCount];
      arrayPointer.read(0, paramRefs, 0, paramCount);
      for (int i = 0; i < paramCount; i++) {
        LLVMLibrary.LLVMTypeRef paramRef = new LLVMLibrary.LLVMTypeRef(paramRefs[i]);
        params.add(new TypeRef(paramRef));
      }
    }

    return params;
  }

  /**
   * Get the number of elements defined inside this structure type. Only works if this is a
   * structure type.
   */
  public int countStructElementTypes() {
    return LLVMLibrary.LLVMCountStructElementTypes(type);
  }

  /** Get the elements within this structure. Only works if this is a structure type. */
  public List<TypeRef> getStructElementTypes() {
    int memberCount = countParamTypes();
    List<TypeRef> members = new ArrayList<>(memberCount);

    if (memberCount > 0) {
      int typeRefSize = Native.getNativeSize(LLVMLibrary.LLVMTypeRef.class);
      Memory arrayPointer = new Memory(memberCount * typeRefSize);
      LLVMLibrary.LLVMTypeRef typeRefArray = new LLVMLibrary.LLVMTypeRef(arrayPointer);
      LLVMLibrary.LLVMGetStructElementTypes(type, typeRefArray);

      Pointer[] memberRefs = new Pointer[memberCount];
      arrayPointer.read(0, memberRefs, 0, memberCount);

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

  public String getStructName() throws LLVMException {
    if (isStructNamed()) {
      return LLVMLibrary.LLVMGetStructName(type);
    } else {
      throw new LLVMException("Type is not named struct");
    }
  }

  /** Determines whether this structure is packed. Only works if this is a structure type. */
  public boolean isPackedStruct() {
    LLVMLibrary.LLVMBool b = LLVMLibrary.LLVMIsPackedStruct(type);
    return Utils.llvmBoolToJavaBool(b);
  }

  /** Determines whether this structure is opaque. Only works if this is a structure type. */
  public boolean isOpaqueStruct() {
    LLVMLibrary.LLVMBool b = LLVMLibrary.LLVMIsOpaqueStruct(type);
    return Utils.llvmBoolToJavaBool(b);
  }

  /**
   * Returns the type of elements within this sequential type. This only works on array, vector, and
   * pointer types.
   */
  public TypeRef getElementType() {
    return new TypeRef(LLVMLibrary.LLVMGetElementType(type));
  }

  /**
   * Returns the type of element at index idx.
   *
   * This must be a struct type.
   */
  public TypeRef getTypeAtIndex(int idx) {
    assert(getTypeKind() == TypeKind.Struct);
    return new TypeRef(LLVMLibrary.LLVMStructGetTypeAtIndex(type, idx));
  }

  /** Returns the length of this array type. This only works on array types. */
  public int getArrayLength() {
    return LLVMLibrary.LLVMGetArrayLength(type);
  }

  /** Returns the address space of this pointer type. This only works on pointer types. */
  public int getPointerAddressSpace() {
    return LLVMLibrary.LLVMGetPointerAddressSpace(type);
  }

  /** Returns the number of elements in this vector type. This only works on vector types. */
  public int getVectorSize() {
    return LLVMLibrary.LLVMGetVectorSize(type);
  }

  /** Returns the alignment of this type. */
  public Value alignOf() {
    return new Value(LLVMLibrary.LLVMAlignOf(type));
  }

  /** Returns the size of this type. */
  public Value sizeOf() {
    return new Value(LLVMLibrary.LLVMSizeOf(type));
  }
}
