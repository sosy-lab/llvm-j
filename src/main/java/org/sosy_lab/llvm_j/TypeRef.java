/*
 * llvm-j  is a library for parsing and modification of LLVM IR in Java.
 * This file is part of llvm-j.
 *
 * Copyright (C) 2012 Kevin Kelly
 * Copyright (C) 2013 Richard Lincoln
 * Copyright (C) 2017 Marek Chalupa, Dirk Beyer
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

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import java.util.ArrayList;
import java.util.List;
import org.sosy_lab.llvm_j.binding.LLVMLibrary;

/** Each value in the LLVM IR has a type, an LLVMTypeRef. */
public class TypeRef {

  /** Types in LLVM IR */
  public enum TypeKind {
    /** Void, i.e., no type */
    Void,
    /** 16 bit floating point type */
    Half,
    /** 32 bit floating point type */
    Float,
    /** 64 bit floating point type */
    Double,
    /** 80 bit floating point type (X87) */
    X86_FP80,
    /** 128 bit floating point type (112-bit mantissa) */
    FP128,
    /** 128 bit floating point type (two 64-bits) */
    PPC_FP128,
    /** Labels */
    Label,
    /** Arbitrary bit width integers */
    Integer,
    /** Functions */
    Function,
    /** Structures */
    Struct,
    /** Arrays */
    Array,
    /** Pointers */
    Pointer,
    /** SIMD 'packed' format, or other vector type */
    Vector,
    /** Metadata */
    Metadata,
    /** X86 MMX */
    X86_MMX,
    /** Tokens */
    Token,
  }

  private LLVMLibrary.LLVMTypeRef type;

  LLVMLibrary.LLVMTypeRef type() {
    return type;
  }

  TypeRef(LLVMLibrary.LLVMTypeRef type) {
    checkNotNull(type);
    this.type = type;
  }

  /** Returns the enumerated type of this type instance. */
  public TypeKind getTypeKind() {
    int typeInt = LLVMLibrary.LLVMGetTypeKind(type);

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

  /**
   * Returns the offset of this struct to the specified index, based on the given data layout.
   *
   * @param idx the index of the element to return the offset for
   * @param td the data layout to assume
   * @return the offset of the given element index from the address of this struct
   * @throws LLVMException if this type is not a struct
   */
  public long getOffsetOfElement(int idx, LLVMLibrary.LLVMTargetDataRef td) throws LLVMException {
    checkNotNull(td);
    if (getTypeKind() != TypeKind.Struct) {
      throw new LLVMException("Type is not a struct");
    }
    return LLVMLibrary.LLVMOffsetOfElement(td, type, idx);
  }

  void dump() {
    LLVMLibrary.LLVMDumpType(type);
  }

  /** Returns the {@link Context} with which this type instance is associated. */
  public Context getTypeContext() {
    return Context.getTypeContext(this);
  }

  /** Get width of this type. This type has to be an integer type. */
  public int getIntTypeWidth() throws LLVMException {
    if (getTypeKind() != TypeKind.Integer) {
      throw new LLVMException("Type is not an integer");
    }
    return LLVMLibrary.LLVMGetIntTypeWidth(type);
  }

  /** Returns whether this is a function type. */
  public boolean isFunctionVarArg() throws LLVMException {
    if (getTypeKind() != TypeKind.Function) {
      throw new LLVMException("Type is not a function");
    }
    LLVMLibrary.LLVMBool b = LLVMLibrary.LLVMIsFunctionVarArg(type);
    return Utils.llvmBoolToJavaBool(b);
  }

  /** Returns the type this function type returns. Only works if this is a function type. */
  public TypeRef getReturnType() throws LLVMException {
    if (getTypeKind() != TypeKind.Function) {
      throw new LLVMException("Type is not a function");
    }
    return new TypeRef(LLVMLibrary.LLVMGetReturnType(type));
  }

  /**
   * Returns the number of parameters this function type accepts. Only works if this is a function
   * type.
   */
  public int countParamTypes() throws LLVMException {
    if (getTypeKind() != TypeKind.Function) {
      throw new LLVMException("Type is not a function");
    }
    return LLVMLibrary.LLVMCountParamTypes(type);
  }

  /** Returns the types of a function's parameters. Only works if this is a function type. */
  public List<TypeRef> getParamTypes() throws LLVMException {
    if (getTypeKind() != TypeKind.Function) {
      throw new LLVMException("Type is not a function");
    }
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
  public int countStructElementTypes() throws LLVMException {
    if (getTypeKind() != TypeKind.Struct) {
      throw new LLVMException("Type is not a struct");
    }
    return LLVMLibrary.LLVMCountStructElementTypes(type);
  }

  /** Get the elements within this structure. Only works if this is a structure type. */
  public List<TypeRef> getStructElementTypes() throws LLVMException {
    if (getTypeKind() != TypeKind.Struct) {
      throw new LLVMException("Type is not a struct");
    }

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

  /** Returns whether this type is a named struct. */
  public boolean isStructNamed() {
    String name = LLVMLibrary.LLVMGetStructName(type);
    return name != null;
  }

  /**
   * Return the name of this type. This type has to be a named struct.
   *
   * @return the name of this type, if it is a named struct
   * @throws LLVMException if this type is not a named struct
   * @see #isStructNamed()
   */
  public String getStructName() throws LLVMException {
    if (isStructNamed()) {
      return LLVMLibrary.LLVMGetStructName(type);
    } else {
      throw new LLVMException("Type is not named struct");
    }
  }

  /** Determines whether this structure is packed. Only works if this is a structure type. */
  public boolean isPackedStruct() throws LLVMException {
    if (getTypeKind() != TypeKind.Struct) {
      throw new LLVMException("Type is not a struct");
    }
    LLVMLibrary.LLVMBool b = LLVMLibrary.LLVMIsPackedStruct(type);
    return Utils.llvmBoolToJavaBool(b);
  }

  /** Determines whether this structure is opaque. Only works if this is a structure type. */
  public boolean isOpaqueStruct() throws LLVMException {
    if (getTypeKind() != TypeKind.Struct) {
      throw new LLVMException("Type is not a struct");
    }
    LLVMLibrary.LLVMBool b = LLVMLibrary.LLVMIsOpaqueStruct(type);
    return Utils.llvmBoolToJavaBool(b);
  }

  /**
   * Returns the type of elements within this sequential type. This only works on array, vector, and
   * pointer types.
   */
  public TypeRef getElementType() throws LLVMException {
    TypeKind typeKind = getTypeKind();
    if (typeKind != TypeKind.Array && typeKind != TypeKind.Vector && typeKind != TypeKind.Pointer) {
      throw new LLVMException("Type neither array, nor vector, nor pointer");
    }
    return new TypeRef(LLVMLibrary.LLVMGetElementType(type));
  }

  /**
   * Returns the type of element at index idx.
   *
   * <p>This must be a struct type.
   */
  public TypeRef getTypeAtIndex(int idx) throws LLVMException {
    if (getTypeKind() != TypeKind.Struct) {
      throw new LLVMException("Type is not a struct");
    }
    return new TypeRef(LLVMLibrary.LLVMStructGetTypeAtIndex(type, idx));
  }

  /** Returns the length of this array type. This only works on array types. */
  public int getArrayLength() throws LLVMException {
    if (getTypeKind() != TypeKind.Array) {
      throw new LLVMException("Type is not an array");
    }
    return LLVMLibrary.LLVMGetArrayLength(type);
  }

  /** Returns the address space of this pointer type. This only works on pointer types. */
  public int getPointerAddressSpace() throws LLVMException {
    if (getTypeKind() != TypeKind.Pointer) {
      throw new LLVMException("Type is not a pointer");
    }
    return LLVMLibrary.LLVMGetPointerAddressSpace(type);
  }

  /** Returns the number of elements in this vector type. This only works on vector types. */
  public int getVectorSize() throws LLVMException {
    if (getTypeKind() != TypeKind.Vector) {
      throw new LLVMException("Type is not a vector");
    }
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
