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
import static org.sosy_lab.llvm_j.Utils.checkLlvmState;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import java.util.ArrayList;
import java.util.List;
import org.sosy_lab.llvm_j.binding.LLVMLibrary;

/** Type of a value in the LLVM IR. */
public class TypeRef {

  /** Types in LLVM IR. */
  public enum TypeKind {
    /** Void type, i.e., no type. */
    Void,
    /** 16 bit floating point type. */
    Half,
    /** 32 bit floating point type. */
    Float,
    /** 64 bit floating point type. */
    Double,
    /** 80 bit floating point type (X87). */
    X86_FP80,
    /** 128 bit floating point type (112-bit mantissa). */
    FP128,
    /** 128 bit floating point type (two 64-bits). */
    PPC_FP128,
    /** Label type. */
    Label,
    /** Type of arbitrary bit width integers. */
    Integer,
    /** Function type. */
    Function,
    /** Structure type. */
    Struct,
    /** Array type. */
    Array,
    /** Pointer type. */
    Pointer,
    /** SIMD 'packed' format, or other vector type. */
    Vector,
    /** Type of LLVM metadata. */
    Metadata,
    /** X86 MMX type. */
    X86_MMX,
    /** Type of tokens. */
    Token,
  }

  private final LLVMLibrary.LLVMTypeRef type;

  public LLVMLibrary.LLVMTypeRef type() {
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
   * @throws IllegalStateException if this type is not a struct
   * @see #getTypeKind()
   */
  public long getOffsetOfElement(int idx, LLVMLibrary.LLVMTargetDataRef td) {
    checkNotNull(td);
    checkLlvmState(getTypeKind().equals(TypeKind.Struct), "Type is not a struct");
    return LLVMLibrary.LLVMOffsetOfElement(td, type, idx);
  }

  /** Returns the {@link Context} with which this type instance is associated. */
  public Context getTypeContext() {
    return Context.getTypeContext(this);
  }

  /**
   * Get width of this type. This type has to be an integer type.
   *
   * @throws IllegalStateException if this type is not an integer type
   * @see #getTypeKind()
   */
  public int getIntTypeWidth() {
    checkLlvmState(getTypeKind().equals(TypeKind.Integer), "Type is not an integer");

    return LLVMLibrary.LLVMGetIntTypeWidth(type);
  }

  /**
   * Returns whether this is a function with var args.
   *
   * @throws IllegalStateException if this type is not a function type
   * @see #getTypeKind()
   */
  public boolean isFunctionVarArg() {
    checkLlvmState(getTypeKind().equals(TypeKind.Function), "Type is not a function");

    LLVMLibrary.LLVMBool b = LLVMLibrary.LLVMIsFunctionVarArg(type);
    return Utils.llvmBoolToJavaBool(b);
  }

  /**
   * Returns the type this function type returns. Only works if this is a function type.
   *
   * @throws IllegalStateException if this type is not a function type
   * @see #getTypeKind()
   */
  public TypeRef getReturnType() {
    checkLlvmState(getTypeKind().equals(TypeKind.Function), "Type is not a function");

    return new TypeRef(LLVMLibrary.LLVMGetReturnType(type));
  }

  /**
   * Returns the number of parameters this function type accepts. Only works if this is a function
   * type.
   *
   * @throws IllegalStateException if this type is not a function type s@see #getTypeKind()
   */
  public int countParamTypes() {
    checkLlvmState(
        getTypeKind().equals(TypeKind.Function), "Type is not a function: " + getTypeKind());

    return LLVMLibrary.LLVMCountParamTypes(type);
  }

  /**
   * Returns the types of a function's parameters. Only works if this is a function type.
   *
   * @throws IllegalStateException if this type is not a function type
   * @see #getTypeKind()
   */
  public List<TypeRef> getParamTypes() {
    checkLlvmState(getTypeKind().equals(TypeKind.Function), "Type is not a function");

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
   *
   * @throws IllegalStateException if this type is not a struct type
   * @see #getTypeKind()
   */
  public int countStructElementTypes() {
    checkLlvmState(getTypeKind().equals(TypeKind.Struct), "Type is not a struct");
    return LLVMLibrary.LLVMCountStructElementTypes(type);
  }

  /**
   * Get the elements within this structure. Only works if this is a structure type.
   *
   * @throws IllegalStateException if this type is not a struct type
   * @see #getTypeKind()
   */
  public List<TypeRef> getStructElementTypes() {
    checkLlvmState(getTypeKind().equals(TypeKind.Struct), "Type is not a struct");

    int memberCount = countStructElementTypes();
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
   * @throws IllegalStateException if this type is not a named struct
   * @see #isStructNamed()
   */
  public String getStructName() {
    checkLlvmState(isStructNamed(), "Type is not named struct");

    return LLVMLibrary.LLVMGetStructName(type);
  }

  /**
   * Determines whether this structure is packed. Only works if this is a structure type.
   *
   * @throws IllegalStateException if this type is not a struct type
   * @see #getTypeKind()
   */
  public boolean isPackedStruct() {
    checkLlvmState(getTypeKind().equals(TypeKind.Struct), "Type is not a struct");
    LLVMLibrary.LLVMBool b = LLVMLibrary.LLVMIsPackedStruct(type);
    return Utils.llvmBoolToJavaBool(b);
  }

  /**
   * Determines whether this structure is opaque. Only works if this is a structure type.
   *
   * @throws IllegalStateException if this type is not a struct type
   * @see #getTypeKind()
   */
  public boolean isOpaqueStruct() {
    checkLlvmState(getTypeKind().equals(TypeKind.Struct), "Type is not a struct");
    LLVMLibrary.LLVMBool b = LLVMLibrary.LLVMIsOpaqueStruct(type);
    return Utils.llvmBoolToJavaBool(b);
  }

  /**
   * Returns the type of elements within this sequential type. This only works on array, vector, and
   * pointer types.
   *
   * @throws IllegalStateException if this type is not an array, vector or pointer type
   * @see #getTypeKind()
   */
  public TypeRef getElementType() {
    TypeKind typeKind = getTypeKind();
    checkLlvmState(
        typeKind.equals(TypeKind.Array)
            || typeKind.equals(TypeKind.Vector)
            || typeKind.equals(TypeKind.Pointer),
        "Type neither array, nor vector, nor pointer");

    return new TypeRef(LLVMLibrary.LLVMGetElementType(type));
  }

  /**
   * Returns the type of element at index idx.
   *
   * @throws IllegalStateException if this type is not a struct type
   * @see #getTypeKind()
   */
  public TypeRef getTypeAtIndex(int idx) {
    checkLlvmState(getTypeKind().equals(TypeKind.Struct), "Type is not a struct");
    return new TypeRef(LLVMLibrary.LLVMStructGetTypeAtIndex(type, idx));
  }

  /**
   * Returns the length of this array type. This only works on array types.
   *
   * @throws IllegalStateException if this type is not an array type
   * @see #getTypeKind()
   */
  public int getArrayLength() {
    checkLlvmState(getTypeKind().equals(TypeKind.Array), "Type is not an array");
    return LLVMLibrary.LLVMGetArrayLength(type);
  }

  /**
   * Returns the address space of this pointer type. This only works on pointer types.
   *
   * @throws IllegalStateException if this type is not a pointer type
   * @see #getTypeKind()
   */
  public int getPointerAddressSpace() {
    checkLlvmState(getTypeKind().equals(TypeKind.Pointer), "Type is not a pointer");
    return LLVMLibrary.LLVMGetPointerAddressSpace(type);
  }

  /**
   * Returns the number of elements in this vector type. This only works on vector types.
   *
   * @throws IllegalStateException if this type is not a vector type
   * @see #getTypeKind()
   */
  public int getVectorSize() {
    checkLlvmState(getTypeKind().equals(TypeKind.Vector), "Type is not a vector");
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
