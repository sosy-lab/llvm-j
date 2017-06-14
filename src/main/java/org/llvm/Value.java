package org.llvm;

import org.bridj.IntValuedEnum;
import org.bridj.Pointer;
import org.bridj.Pointer.StringType;

import java.util.ArrayList;
import java.util.List;

import static org.llvm.binding.LLVMLibrary.*;

/**
 * Represents an individual value in LLVM IR.
 */
public class Value {

    private LLVMValueRef value;

    LLVMValueRef value() {
        return value;
    }

    public boolean equals(Value rhs) {
        return value.getPeer() == rhs.value.getPeer();
    }

    Value(LLVMValueRef value) {
        this.value = value;
    }

    /**
     * Obtain the type of a value.<br>
     *
     * @see llvm::Value::getType()
     */
    public TypeRef typeOf() {
        return new TypeRef(LLVMTypeOf(value));
    }

    /**
     * Obtain the string name of a value.<br>
     *
     * @see llvm::Value::getName()
     */
    public String getValueName() {
        return LLVMGetValueName(value).getCString();
    }

    /**
     * Set the string name of a value.<br>
     *
     * @see llvm::Value::setName()
     */
    public void setValueName(String name) {
        LLVMSetValueName(value, Pointer.pointerToCString(name));
    }

    /**
     * Dump a representation of a value to stderr.<br>
     *
     * @see llvm::Value::dump()
     */
    public void dumpValue() {
        LLVMDumpValue(value);
    }

    public String toString() {
	  Pointer<Byte > ret = LLVMPrintValueToString(value);
      return ret.getString(StringType.C);
    }

    /**
     * Replace all uses of a value with another one.<br>
     *
     * @see llvm::Value::replaceAllUsesWith()
     */
    public void replaceAllUsesWith(Value newVal) {
        LLVMReplaceAllUsesWith(value, newVal.value());
    }

    /**
     * Determine whether an instruction has any metadata attached.
     */
    public int hasMetadata() {
        return LLVMHasMetadata(value);
    }

    /**
     * Return metadata associated with an instruction value.
     */
    public Value getMetadata(int kindID) {
        return new Value(LLVMGetMetadata(value, kindID));
    }

    /**
     * Set metadata associated with an instruction value.
     */
    public void setMetadata(int kindID, Value node) {
        LLVMSetMetadata(value, kindID, node.value());
    }

    /**
     * Check the type of value.
     * C Conversion functions return the input value if it is an instance of the
     * specified class, otherwise NULL. (@see llvm::dyn_cast_or_null).
     */
    public boolean isArgument() {
        return LLVMIsAArgument(value) != null;
    }

    public boolean isABasicBlock() {
        return LLVMIsABasicBlock(value) != null;
    }

    public boolean isInlineAsm() {
        return LLVMIsAInlineAsm(value) != null;
    }

    public boolean isUser() {
        return LLVMIsAUser(value) != null;
    }

    public boolean isAConstant() {
        return LLVMIsAConstant(value) != null;
    }

    public boolean isConstantAggregateZero() {
        return LLVMIsAConstantAggregateZero(value) != null;
    }

    public boolean isConstantArray() {
        return LLVMIsAConstantArray(value) != null;
    }

    public boolean isConstantExpr() {
        return LLVMIsAConstantExpr(value) != null;
    }

    public boolean isConstantFP() {
        return LLVMIsAConstantFP(value) != null;
    }

    public boolean isConstantInt() {
        return LLVMIsAConstantInt(value) != null;
    }

    public boolean isConstantPointerNull() {
        return LLVMIsAConstantPointerNull(value) != null;
    }

    public boolean isConstantStruct() {
        return LLVMIsAConstantStruct(value) != null;
    }

    public boolean isConstantVector() {
        return LLVMIsAConstantVector(value) != null;
    }

    public boolean isGlobalValue() {
        return LLVMIsAGlobalValue(value) != null;
    }

    public boolean isFunction() {
        return LLVMIsAFunction(value) != null;
    }

    public boolean isGlobalAlias() {
        return LLVMIsAGlobalAlias(value) != null;
    }

    public boolean isGlobalVariable() {
        return LLVMIsAGlobalVariable(value) != null;
    }

    public boolean isUndefValue() {
        return LLVMIsAUndefValue(value) != null;
    }

    public boolean isInstruction() {
        return LLVMIsAInstruction(value) != null;
    }

    public boolean isBinaryOperator() {
        return LLVMIsABinaryOperator(value) != null;
    }

    public boolean isCallInst() {
        return LLVMIsACallInst(value) != null;
    }

    public boolean isIntrinsicInst() {
        return LLVMIsAIntrinsicInst(value) != null;
    }

    public boolean isDbgInfoIntrinsic() {
        return LLVMIsADbgInfoIntrinsic(value) != null;
    }

    public boolean isDbgDeclareInst() {
        return LLVMIsADbgDeclareInst(value) != null;
    }

    //	public boolean isEHSelectorInst() { return LLVMIsAEHSelectorInst(value) != null; }
    public boolean isMemIntrinsic() {
        return LLVMIsAMemIntrinsic(value) != null;
    }

    public boolean isMemCpyInst() {
        return LLVMIsAMemCpyInst(value) != null;
    }

    public boolean isMemMoveInst() {
        return LLVMIsAMemMoveInst(value) != null;
    }

    public boolean isMemSetInst() {
        return LLVMIsAMemSetInst(value) != null;
    }

    public boolean isCmpInst() {
        return LLVMIsACmpInst(value) != null;
    }

    public boolean isFCmpInst() {
        return LLVMIsAFCmpInst(value) != null;
    }

    public boolean isICmpInst() {
        return LLVMIsAICmpInst(value) != null;
    }

    public boolean isExtractElementInst() {
        return LLVMIsAExtractElementInst(value) != null;
    }

    public boolean isGetElementPtrInst() {
        return LLVMIsAGetElementPtrInst(value) != null;
    }

    public boolean isInsertElementInst() {
        return LLVMIsAInsertElementInst(value) != null;
    }

    public boolean isInsertValueInst() {
        return LLVMIsAInsertValueInst(value) != null;
    }

    public boolean isPHINode() {
        return LLVMIsAPHINode(value) != null;
    }

    public boolean isSelectInst() {
        return LLVMIsASelectInst(value) != null;
    }

    public boolean isShuffleVectorInst() {
        return LLVMIsAShuffleVectorInst(value) != null;
    }

    public boolean isStoreInst() {
        return LLVMIsAStoreInst(value) != null;
    }

    public boolean isTerminatorInst() {
        return LLVMIsATerminatorInst(value) != null;
    }

    public boolean isBranchInst() {
        return LLVMIsABranchInst(value) != null;
    }

    public boolean isInvokeInst() {
        return LLVMIsAInvokeInst(value) != null;
    }

    public boolean isReturnInst() {
        return LLVMIsAReturnInst(value) != null;
    }

    public boolean isSwitchInst() {
        return LLVMIsASwitchInst(value) != null;
    }

    public boolean isUnreachableInst() {
        return LLVMIsAUnreachableInst(value) != null;
    }

    //	public boolean isUnwindInst() { return LLVMIsAUnwindInst(value) != null; }
    public boolean isUnaryInstruction() {
        return LLVMIsAUnaryInstruction(value) != null;
    }

    public boolean isAllocaInst() {
        return LLVMIsAAllocaInst(value) != null;
    }

    public boolean isCastInst() {
        return LLVMIsACastInst(value) != null;
    }

    public boolean isBitCastInst() {
        return LLVMIsABitCastInst(value) != null;
    }

    public boolean isFPExtInst() {
        return LLVMIsAFPExtInst(value) != null;
    }

    public boolean isFPToSIInst() {
        return LLVMIsAFPToSIInst(value) != null;
    }

    public boolean isFPToUIInst() {
        return LLVMIsAFPToUIInst(value) != null;
    }

    public boolean isFPTruncInst() {
        return LLVMIsAFPTruncInst(value) != null;
    }

    public boolean isIntToPtrInst() {
        return LLVMIsAIntToPtrInst(value) != null;
    }

    public boolean isPtrToIntInst() {
        return LLVMIsAPtrToIntInst(value) != null;
    }

    public boolean isSExtInst() {
        return LLVMIsASExtInst(value) != null;
    }

    public boolean isSIToFPInst() {
        return LLVMIsASIToFPInst(value) != null;
    }

    public boolean isTruncInst() {
        return LLVMIsATruncInst(value) != null;
    }

    public boolean isUIToFPInst() {
        return LLVMIsAUIToFPInst(value) != null;
    }

    public boolean isZExtInst() {
        return LLVMIsAZExtInst(value) != null;
    }

    public boolean isExtractValueInst() {
        return LLVMIsAExtractValueInst(value) != null;
    }

    public boolean isLoadInst() {
        return LLVMIsALoadInst(value) != null;
    }

    public boolean isVAArgInst() {
        return LLVMIsAVAArgInst(value) != null;
    }

    /**
     * Determine whether the specified constant instance is constant.
     */
    public boolean isConstant() {
        return LLVMIsConstant(value) != 0;
    }

    /**
     * Determine whether a value instance is null.<br>
     *
     * @see llvm::Constant::isNullValue()
     */
    public boolean isNull() {
        return LLVMIsNull(value) != 0;
    }

    /**
     * Determine whether a value instance is undefined.
     */
    public boolean isUndef() {
        return LLVMIsUndef(value) != 0;
    }

    // TODO: move
    public static native LLVMUseRef LLVMGetFirstUse(LLVMValueRef val);

    // TODO: move
    public static native LLVMUseRef LLVMGetNextUse(LLVMUseRef u);

    /**
     * Obtain the user value for a user.<br>
     * The returned value corresponds to a llvm::User type.<br>
     *
     * @see llvm::Use::getUser()
     */
    // TODO: move
    public static Value getUser(LLVMUseRef u) {
        return new Value(LLVMGetUser(u));
    }

    /**
     * Obtain the value this use corresponds to.<br>
     *
     * @see llvm::Use::get()
     */
    // TODO: move
    public static Value getUsedValue(LLVMUseRef u) {
        return new Value(LLVMGetUsedValue(u));
    }

    /**
     * Obtain an operand at a specific index in a llvm::User value.<br>
     *
     * @see llvm::User::getOperand()
     */
    public Value getOperand(int index) {
        return new Value(LLVMGetOperand(value, index));
    }

    /**
     * Set an operand at a specific index in a llvm::User value.<br>
     *
     * @see llvm::User::setOperand()
     */
    public void setOperand(int index, Value val) {
        LLVMSetOperand(value, index, val.value());
    }

    /**
     * Obtain the number of operands in a llvm::User value.<br>
     *
     * @see llvm::User::getNumOperands()
     */
    public int getNumOperands() {
        return LLVMGetNumOperands(value);
    }

    // MetaData
    /*public ValueRef MDStringInContext(LLVMContextRef c, Pointer<Byte> str,
            int sLen) {
        return new ValueRef(LLVMMDStringInContext(value));
    }

    public ValueRef MDString(Pointer<Byte> str, int sLen) {
        return new ValueRef(LLVMMDString(value));
    }

    public ValueRef MDNodeInContext(LLVMContextRef c,
            Pointer<LLVMValueRef> vals, int count) {
        return new ValueRef(LLVMMDNodeInContext(value));
    }

    public ValueRef MDNode(Pointer<LLVMValueRef> vals, int count) {
        return new ValueRef(LLVMMDNode(value));
    }*/

    /**
     * Obtain the zero extended value for an integer constant value.<br>
     *
     * @see llvm::ConstantInt::getZExtValue()
     */
    public long constIntGetZExtValue() {
        return LLVMConstIntGetZExtValue(value);
    }

    /**
     * Obtain the sign extended value for an integer constant value.<br>
     *
     * @see llvm::ConstantInt::getSExtValue()
     */
    public long constIntGetSExtValue() {
        return LLVMConstIntGetSExtValue(value);
    }

    /**
     * Create a ConstantDataSequential and initialize it with a string.<br>
     *
     * @see llvm::ConstantDataArray::getString()
     */
    public static Value constStringInContext(Context c, String str, int length,
            boolean dontNullTerminate) {
        return new Value(LLVMConstStringInContext(c.context(),
                Pointer.pointerToCString(str), length, dontNullTerminate ? 1
                        : 0));
    }

    /*public ValueRef constStructInContext(Context c, ValueRef[] constantVals,
            int count, boolean packed) {
        return new ValueRef(LLVMConstStructInContext(C.context(), constantVals,
                count, packed ? 1 : 0));
    }*/

    /**
     * Create a ConstantDataSequential with string content in the global
     * context.<br>
     * This is the same as LLVMConstStringInContext except it operates on the<br>
     * global context.<br>
     *
     * @see LLVMConstStringInContext()<br>
     * @see llvm::ConstantDataArray::getString()
     */
    public static Value constString(String str, int length,
            boolean dontNullTerminate) {
        return new Value(LLVMConstString(Pointer.pointerToCString(str), length,
                dontNullTerminate ? 1 : 0));
    }

    /**
     * Create a ConstantArray from values.<br>
     *
     * @see llvm::ConstantArray::get()
     */
    // TODO: change Pointer to array
    public static Value constArray(TypeRef elementTy,
            Pointer<LLVMValueRef> constantVals, int length) {
        return new Value(LLVMConstArray(elementTy.type(), constantVals, length));
    }

    /**
     * Create a ConstantStruct in the global Context.<br>
     * This is the same as LLVMConstStructInContext except it operates on the<br>
     * global Context.<br>
     *
     * @see LLVMConstStructInContext()
     */
    public static Value constStruct(Pointer<LLVMValueRef> constantVals,
            int count, boolean packed) {
        return new Value(LLVMConstStruct(constantVals, count, packed ? 1 : 0));
    }

    /**
     * Create a non-packed constant structure in the global context.
     */
    public static Value constStruct(Value... constantVals) {
        return new Value(LLVMConstStruct(internalize(constantVals),
                constantVals.length, 0));
    }

    /**
     * Create a ConstantVector from values.<br>
     *
     * @see llvm::ConstantVector::get()
     */
    // TODO: change Pointer to array
    public static Value constVector(Pointer<LLVMValueRef> scalarConstantVals,
            int size) {
        return new Value(LLVMConstVector(scalarConstantVals, size));
    }

    public static native IntValuedEnum<LLVMOpcode> GetConstOpcode(
            LLVMValueRef constantVal);

    public Value constNeg() {
        return new Value(LLVMConstNeg(value));
    }

    public Value constNSWNeg() {
        return new Value(LLVMConstNSWNeg(value));
    }

    public Value constNUWNeg() {
        return new Value(LLVMConstNUWNeg(value));
    }

    public Value constFNeg() {
        return new Value(LLVMConstFNeg(value));
    }

    public Value constNot() {
        return new Value(LLVMConstNot(value));
    }

    public static Value constAdd(Value lhsConstant, Value rhsConstant) {
        return new Value(LLVMConstAdd(lhsConstant.value(), rhsConstant.value()));
    }

    public static Value constNSWAdd(Value lhsConstant, Value rhsConstant) {
        return new Value(LLVMConstNSWAdd(lhsConstant.value(),
                rhsConstant.value()));
    }

    public static Value constNUWAdd(Value lhsConstant, Value rhsConstant) {
        return new Value(LLVMConstNUWAdd(lhsConstant.value(),
                rhsConstant.value()));
    }

    public static Value constFAdd(Value lhsConstant, Value rhsConstant) {
        return new Value(
                LLVMConstFAdd(lhsConstant.value(), rhsConstant.value()));
    }

    public static Value constSub(Value lhsConstant, Value rhsConstant) {
        return new Value(LLVMConstSub(lhsConstant.value(), rhsConstant.value()));
    }

    public static Value constNSWSub(Value lhsConstant, Value rhsConstant) {
        return new Value(LLVMConstNSWSub(lhsConstant.value(),
                rhsConstant.value()));
    }

    public static Value constNUWSub(Value lhsConstant, Value rhsConstant) {
        return new Value(LLVMConstNUWSub(lhsConstant.value(),
                rhsConstant.value()));
    }

    public static Value constFSub(Value lhsConstant, Value rhsConstant) {
        return new Value(
                LLVMConstFSub(lhsConstant.value(), rhsConstant.value()));
    }

    public static Value constMul(Value lhsConstant, Value rhsConstant) {
        return new Value(LLVMConstMul(lhsConstant.value(), rhsConstant.value()));
    }

    public static Value constNSWMul(Value lhsConstant, Value rhsConstant) {
        return new Value(LLVMConstNSWMul(lhsConstant.value(),
                rhsConstant.value()));
    }

    public static Value constNUWMul(Value lhsConstant, Value rhsConstant) {
        return new Value(LLVMConstNUWMul(lhsConstant.value(),
                rhsConstant.value()));
    }

    public static Value constFMul(Value lhsConstant, Value rhsConstant) {
        return new Value(
                LLVMConstFMul(lhsConstant.value(), rhsConstant.value()));
    }

    public static Value constUDiv(Value lhsConstant, Value rhsConstant) {
        return new Value(
                LLVMConstUDiv(lhsConstant.value(), rhsConstant.value()));
    }

    public static Value constSDiv(Value lhsConstant, Value rhsConstant) {
        return new Value(
                LLVMConstSDiv(lhsConstant.value(), rhsConstant.value()));
    }

    public static Value constExactSDiv(Value lhsConstant, Value rhsConstant) {
        return new Value(LLVMConstExactSDiv(lhsConstant.value(),
                rhsConstant.value()));
    }

    public static Value constFDiv(Value lhsConstant, Value rhsConstant) {
        return new Value(
                LLVMConstFDiv(lhsConstant.value(), rhsConstant.value()));
    }

    public static Value constURem(Value lhsConstant, Value rhsConstant) {
        return new Value(
                LLVMConstURem(lhsConstant.value(), rhsConstant.value()));
    }

    public static Value constSRem(Value lhsConstant, Value rhsConstant) {
        return new Value(
                LLVMConstSRem(lhsConstant.value(), rhsConstant.value()));
    }

    public static Value constFRem(Value lhsConstant, Value rhsConstant) {
        return new Value(
                LLVMConstFRem(lhsConstant.value(), rhsConstant.value()));
    }

    public static Value constAnd(Value lhsConstant, Value rhsConstant) {
        return new Value(LLVMConstAnd(lhsConstant.value(), rhsConstant.value()));
    }

    public static Value constOr(Value lhsConstant, Value rhsConstant) {
        return new Value(LLVMConstOr(lhsConstant.value(), rhsConstant.value()));
    }

    public static Value constXor(Value lhsConstant, Value rhsConstant) {
        return new Value(LLVMConstXor(lhsConstant.value(), rhsConstant.value()));
    }

    public static Value constICmp(IntValuedEnum<LLVMIntPredicate> predicate,
            Value lhsConstant, Value rhsConstant) {
        return new Value(LLVMConstICmp(predicate, lhsConstant.value(),
                rhsConstant.value()));
    }

    public static Value constFCmp(IntValuedEnum<LLVMRealPredicate> predicate,
            Value lhsConstant, Value rhsConstant) {
        return new Value(LLVMConstFCmp(predicate, lhsConstant.value(),
                rhsConstant.value()));
    }

    public static Value constShl(Value lhsConstant, Value rhsConstant) {
        return new Value(LLVMConstShl(lhsConstant.value(), rhsConstant.value()));
    }

    public static Value constLShr(Value lhsConstant, Value rhsConstant) {
        return new Value(
                LLVMConstLShr(lhsConstant.value(), rhsConstant.value()));
    }

    public static Value constAShr(Value lhsConstant, Value rhsConstant) {
        return new Value(
                LLVMConstAShr(lhsConstant.value(), rhsConstant.value()));
    }

    // TODO: fix Pointer (change to array)
    public static Value constGEP(Value constantVal,
            Pointer<LLVMValueRef> constantIndices, int numIndices) {
        return new Value(LLVMConstGEP(constantVal.value(), constantIndices,
                numIndices));
    }

    public static Value constInBoundsGEP(Value constantVal,
            Pointer<LLVMValueRef> constantIndices, int numIndices) {
        return new Value(LLVMConstInBoundsGEP(constantVal.value(),
                constantIndices, numIndices));
    }

    public static Value constInBoundsGEP(Value constantVal,
            Value... constantIndices) {
        return new Value(LLVMConstInBoundsGEP(constantVal.value(),
                internalize(constantIndices), constantIndices.length));
    }

    public static Value constTrunc(Value constantVal, TypeRef toType) {
        return new Value(LLVMConstTrunc(constantVal.value(), toType.type()));
    }

    public static Value constSExt(Value constantVal, TypeRef toType) {
        return new Value(LLVMConstSExt(constantVal.value(), toType.type()));
    }

    public static Value constZExt(Value constantVal, TypeRef toType) {
        return new Value(LLVMConstZExt(constantVal.value(), toType.type()));
    }

    public static Value constFPTrunc(Value constantVal, TypeRef toType) {
        return new Value(LLVMConstFPTrunc(constantVal.value(), toType.type()));
    }

    public static Value constFPExt(Value constantVal, TypeRef toType) {
        return new Value(LLVMConstFPExt(constantVal.value(), toType.type()));
    }

    public static Value constUIToFP(Value constantVal, TypeRef toType) {
        return new Value(LLVMConstUIToFP(constantVal.value(), toType.type()));
    }

    public static Value constSIToFP(Value constantVal, TypeRef toType) {
        return new Value(LLVMConstSIToFP(constantVal.value(), toType.type()));
    }

    public static Value constFPToUI(Value constantVal, TypeRef toType) {
        return new Value(LLVMConstFPToUI(constantVal.value(), toType.type()));
    }

    public static Value constFPToSI(Value constantVal, TypeRef toType) {
        return new Value(LLVMConstFPToSI(constantVal.value(), toType.type()));
    }

    public static Value constPtrToInt(Value constantVal, TypeRef toType) {
        return new Value(LLVMConstPtrToInt(constantVal.value(), toType.type()));
    }

    public static Value constIntToPtr(Value constantVal, TypeRef toType) {
        return new Value(LLVMConstIntToPtr(constantVal.value(), toType.type()));
    }

    public static Value constBitCast(Value constantVal, TypeRef toType) {
        return new Value(LLVMConstBitCast(constantVal.value(), toType.type()));
    }

    public static Value constZExtOrBitCast(Value constantVal, TypeRef toType) {
        return new Value(LLVMConstZExtOrBitCast(constantVal.value(),
                toType.type()));
    }

    public static Value constSExtOrBitCast(Value constantVal, TypeRef toType) {
        return new Value(LLVMConstSExtOrBitCast(constantVal.value(),
                toType.type()));
    }

    public static Value constTruncOrBitCast(Value constantVal, TypeRef toType) {
        return new Value(LLVMConstTruncOrBitCast(constantVal.value(),
                toType.type()));
    }

    public static Value constPointerCast(Value constantVal, TypeRef toType) {
        return new Value(LLVMConstPointerCast(constantVal.value(),
                toType.type()));
    }

    public static Value constIntCast(Value constantVal, TypeRef toType,
            boolean isSigned) {
        return new Value(LLVMConstIntCast(constantVal.value(), toType.type(),
                isSigned ? 1 : 0));
    }

    public static Value constFPCast(Value constantVal, TypeRef toType) {
        return new Value(LLVMConstFPCast(constantVal.value(), toType.type()));
    }

    public static Value constSelect(Value constantCondition,
            Value constantIfTrue, Value constantIfFalse) {
        return new Value(LLVMConstSelect(constantCondition.value(),
                constantIfTrue.value(), constantIfFalse.value()));
    }

    public static Value constExtractElement(Value vectorConstant,
            Value indexConstant) {
        return new Value(LLVMConstExtractElement(vectorConstant.value(),
                indexConstant.value()));
    }

    public static Value constInsertElement(Value vectorConstant,
            Value elementValueConstant, Value indexConstant) {
        return new Value(LLVMConstInsertElement(vectorConstant.value(),
                elementValueConstant.value(), indexConstant.value()));
    }

    public static Value constShuffleVector(Value vectorAConstant,
            Value vectorBConstant, Value maskConstant) {
        return new Value(LLVMConstShuffleVector(vectorAConstant.value(),
                vectorBConstant.value(), maskConstant.value()));
    }

    public static Value constExtractValue(Value aggConstant,
            Pointer<Integer> idxList, int numIdx) {
        return new Value(LLVMConstExtractValue(aggConstant.value(), idxList,
                numIdx));
    }

    public static Value constInsertValue(Value aggConstant,
            Value elementValueConstant, Pointer<Integer> idxList, int numIdx) {
        return new Value(LLVMConstInsertValue(aggConstant.value(),
                elementValueConstant.value(), idxList, numIdx));
    }

    public static Value constInlineAsm(TypeRef ty, String asmString,
            String constraints, boolean hasSideEffects, boolean isAlignStack) {
        return new Value(LLVMConstInlineAsm(ty.type(),
                Pointer.pointerToCString(asmString),
                Pointer.pointerToCString(constraints), hasSideEffects ? 1 : 0,
                isAlignStack ? 1 : 0));
    }

    public static Value blockAddress(Value f, BasicBlock bb) {
        return new Value(LLVMBlockAddress(f.value(), bb.bb()));
    }

    public Module getGlobalParent() {
        return new Module(LLVMGetGlobalParent(value));
    }

    public boolean isDeclaration() {
        return LLVMIsDeclaration(value) != 0;
    }

    public IntValuedEnum<LLVMLinkage> getLinkage() {
        return LLVMGetLinkage(value);
    }

    public void setLinkage(IntValuedEnum<LLVMLinkage> linkage) {
        LLVMSetLinkage(value, linkage);
    }

    public String getSection() {
        return LLVMGetSection(value).getCString();
    }

    public void setSection(String section) {
        LLVMSetSection(value, Pointer.pointerToCString(section));
    }

    public IntValuedEnum<LLVMVisibility> getVisibility() {
        return LLVMGetVisibility(value);
    }

    public void setVisibility(IntValuedEnum<LLVMVisibility> viz) {
        LLVMSetVisibility(value, viz);
    }

    public void getAlignment() {
        LLVMGetAlignment(value);
    }

    public void setAlignment(int bytes) {
        LLVMSetAlignment(value, bytes);
    }

    // this.value is GlobalVar
    public Value getNextGlobal() {
        return new Value(LLVMGetNextGlobal(value));
    }

    public Value getPreviousGlobal() {
        return new Value(LLVMGetPreviousGlobal(value));
    }

    // this.value is GlobalVar
    public void deleteGlobal() {
        LLVMDeleteGlobal(value);
    }

    public Value getInitializer() {
        return new Value(LLVMGetInitializer(value));
    }

    public void setInitializer(Value constantVal) {
        LLVMSetInitializer(value, constantVal.value());
    }

    public boolean isThreadLocal() {
        return LLVMIsThreadLocal(value) != 0;
    }

    public void setThreadLocal(boolean isThreadLocal) {
        LLVMSetThreadLocal(value, isThreadLocal ? 1 : 0);
    }

    public boolean isGlobalConstant() {
        return LLVMIsGlobalConstant(value) != 0;
    }

    public void setGlobalConstant(boolean isConstant) {
        LLVMSetGlobalConstant(value, isConstant ? 1 : 0);
    }

    /**
     * Advance a Function iterator to the next Function.<br>
     * Returns null if the iterator was already at the end and there are no more<br>
     * functions.
     */
    public Value getNextFunction() {
        return new Value(LLVMGetNextFunction(value));
    }

    /**
     * Decrement a Function iterator to the previous Function.<br>
     * Returns null if the iterator was already at the beginning and there are<br>
     * no previous functions.
     */
    public Value getPreviousFunction() {
        return new Value(LLVMGetPreviousFunction(value));
    }

    /**
     * Remove a function from its containing module and deletes it.<br>
     *
     * @see llvm::Function::eraseFromParent()
     */
    public void deleteFunction() {
        LLVMDeleteFunction(value);
    }

    /**
     * Obtain the ID number from a function instance.<br>
     *
     * @see llvm::Function::getIntrinsicID()
     */
    public void getIntrinsicID() {
        LLVMGetIntrinsicID(value);
    }

    /**
     * Obtain the calling function of a function.<br>
     * The returned value corresponds to the LLVMCallConv enumeration.<br>
     *
     * @see llvm::Function::getCallingConv()
     */
    public void getFunctionCallConv() {
        LLVMGetFunctionCallConv(value);
    }

    /**
     * Set the calling convention of a function.<br>
     *
     * @see llvm::Function::setCallingConv()<br>
     * @param cc
     *            LLVMCallConv to set calling convention to
     */
    public void setFunctionCallConv(LLVMCallConv cc) {
        LLVMSetFunctionCallConv(value, (int) cc.value());
    }

    /**
     * Obtain the name of the garbage collector to use during code<br>
     * generation.<br>
     *
     * @see llvm::Function::getGC()
     */
    public String getGC() {
        return LLVMGetGC(value).getCString();
    }

    /**
     * Define the garbage collector to use during code generation.<br>
     *
     * @see llvm::Function::setGC()
     */
    public void setGC(String name) {
        LLVMSetGC(value, Pointer.pointerToCString(name));
    }

    /**
     * Obtain an attribute from a function.<br>
     *
     * @see llvm::Function::getAttributes()
    public IntValuedEnum<LLVMAttribute> getFunctionAttr() {
        return LLVMGetFunctionAttr(value);
    }
     */

    /**
     * Obtain the number of parameters in a function.<br>
     *
     * @see llvm::Function::arg_size()
     */
    public int countParams() {
        return LLVMCountParams(value);
    }

    /**
     * Obtain the parameters in a function.
     *
     * @see llvm::Function::arg_begin()
     */
    public List<Value> getParams() {
        int paramCount = countParams();
        Pointer<LLVMValueRef> paramRef = Pointer.allocateArray(LLVMValueRef.class, paramCount);
        LLVMGetParams(value, paramRef);

        List<Value> params = new ArrayList<Value>(paramCount);
        for (int i = 0; i < paramCount; i++) {
            params.add(new Value(paramRef.get(i)));
        }

        return params;
    }

    /**
     * Obtain the parameter at the specified index.<br>
     * Parameters are indexed from 0.<br>
     *
     * @see llvm::Function::arg_begin()
     */
    public Value getParam(int index) {
        return new Value(LLVMGetParam(value, index));
    }

    /**
     * Obtain the function to which this argument belongs.<br>
     * Unlike other functions in this group, this one takes a LLVMValueRef<br>
     * that corresponds to a llvm::Attribute.<br>
     * The returned LLVMValueRef is the llvm::Function to which this<br>
     * argument belongs.
     */
    public Value getParamParent() {
        return new Value(LLVMGetParamParent(value));
    }

    /**
     * Obtain the first parameter to a function.<br>
     *
     * @see llvm::Function::arg_begin()
     */
    public Value getFirstParam() {
        try {
            return new Value(LLVMGetFirstParam(value));
        } catch (java.lang.IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Obtain the last parameter to a function.<br>
     *
     * @see llvm::Function::arg_end()
     */
    public Value getLastParam() {
        try {
            return new Value(LLVMGetLastParam(value));
        } catch (java.lang.IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Obtain the next parameter to a function.<br>
     * This takes a LLVMValueRef obtained from LLVMGetFirstParam() (which is<br>
     * actually a wrapped iterator) and obtains the next parameter from the<br>
     * underlying iterator.
     */
    public Value getNextParam() {
        return new Value(LLVMGetNextParam(value));
    }

    /**
     * Obtain the previous parameter to a function.<br>
     * This is the opposite of LLVMGetNextParam().
     */
    public Value getPreviousParam() {
        return new Value(LLVMGetPreviousParam(value));
    }

    /**
     * Get an attribute from a function argument.
    public IntValuedEnum<LLVMAttribute> getAttribute() {
        return LLVMGetAttribute(value);
    }
     */

    /**
     * Set the alignment for a function parameter.<br>
     *
     * @see llvm::Argument::addAttr()<br>
     * @see llvm::Attribute::constructAlignmentFromInt()
     */
    public void setParamAlignment(int align) {
        LLVMSetParamAlignment(value, align);
    }

    /**
     * Determine whether a LLVMValueRef is itself a basic block.
     */
    public boolean isBasicBlock() {
        return LLVMValueIsBasicBlock(value) != 0;
    }

    /**
     * Convert a LLVMValueRef to a LLVMBasicBlockRef instance.
     */
    public BasicBlock asBasicBlock() {
        return new BasicBlock(LLVMValueAsBasicBlock(value));
    }

    /**
     * Obtain the number of basic blocks in a function.<br>
     *
     * @param Fn
     *            Function value to operate on.
     */
    public int countBasicBlocks() {
        return LLVMCountBasicBlocks(value);
    }

    /**
     * Obtain all of the basic blocks in a function.<br>
     * This operates on a function value. The BasicBlocks parameter is a<br>
     * pointer to a pre-allocated array of LLVMBasicBlockRef of at least<br>
     * LLVMCountBasicBlocks() in length. This array is populated with<br>
     * LLVMBasicBlockRef instances.
     */
    public void getBasicBlocks(Pointer<LLVMBasicBlockRef> basicBlocks) {
        LLVMGetBasicBlocks(value, basicBlocks);
    }

    /**
     * Obtain the first basic block in a function.<br>
     * The returned basic block can be used as an iterator. You will likely<br>
     * eventually call into LLVMGetNextBasicBlock() with it.<br>
     *
     * @see llvm::Function::begin()
     */
    public BasicBlock getFirstBasicBlock() {
        try {
            return new BasicBlock(LLVMGetFirstBasicBlock(value));
        } catch (java.lang.IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Obtain the last basic block in a function.<br>
     *
     * @see llvm::Function::end()
     */
    public BasicBlock getLastBasicBlock() {
        try {
            return new BasicBlock(LLVMGetLastBasicBlock(value));
        } catch (java.lang.IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Obtain the basic block that corresponds to the entry point of a<br>
     * function.<br>
     *
     * @see llvm::Function::getEntryBlock()
     */
    public BasicBlock getEntryBasicBlock() {
        try {
            return new BasicBlock(LLVMGetEntryBasicBlock(value));
        } catch (java.lang.IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Append a basic block to the end of a function.<br>
     *
     * @see llvm::BasicBlock::Create()
     */
    public BasicBlock appendBasicBlockInContext(Context c, String name) {
        return new BasicBlock(LLVMAppendBasicBlockInContext(c.context(), value,
                Pointer.pointerToCString(name)));
    }

    /**
     * Append a basic block to the end of a function using the global<br>
     * context.<br>
     *
     * @see llvm::BasicBlock::Create()
     */
    public BasicBlock appendBasicBlock(String name) {
        return new BasicBlock(LLVMAppendBasicBlock(value,
                Pointer.pointerToCString(name)));
    }

    // Instruction

    /**
     * Obtain the basic block to which an instruction belongs.<br>
     *
     * @see llvm::Instruction::getParent()
     */
    public BasicBlock getInstructionParent() {
        return new BasicBlock(LLVMGetInstructionParent(value));
    }

    /**
     * Obtain the instruction that occurs after the one specified.<br>
     * The next instruction will be from the same basic block.<br>
     * If this is the last instruction in a basic block, NULL will be<br>
     * returned.
     */
    public Value getNextInstruction() {
        return new Value(LLVMGetNextInstruction(value));
    }

    /**
     * Obtain the instruction that occured before this one.<br>
     * If the instruction is the first instruction in a basic block, NULL<br>
     * will be returned.
     */
    public Value getPreviousInstruction() {
        return new Value(LLVMGetPreviousInstruction(value));
    }

    /**
     * Set the calling convention for a call instruction.<br>
     * This expects an LLVMValueRef that corresponds to a llvm::CallInst or<br>
     * llvm::InvokeInst.<br>
     *
     * @see llvm::CallInst::setCallingConv()<br>
     * @see llvm::InvokeInst::setCallingConv()
     */
    public void setInstructionCallConv(int cc) {
        LLVMSetInstructionCallConv(value, cc);
    }

    /**
     * Obtain the calling convention for a call instruction.<br>
     * This is the opposite of LLVMSetInstructionCallConv(). Reads its<br>
     * usage.<br>
     *
     * @see LLVMSetInstructionCallConv()
     */
    public int getInstructionCallConv() {
        return LLVMGetInstructionCallConv(value);
    }
    public void setInstrParamAlignment(int index, int align) {
        LLVMSetInstrParamAlignment(value, index, align);
    }

    /**
     * Obtain whether a call instruction is a tail call.<br>
     * This only works on llvm::CallInst instructions.<br>
     *
     * @see llvm::CallInst::isTailCall()
     */
    public boolean isTailCall() {
        return LLVMIsTailCall(value) != 0;
    }

    /**
     * Set whether a call instruction is a tail call.<br>
     * This only works on llvm::CallInst instructions.<br>
     *
     * @see llvm::CallInst::setTailCall()
     */
    public void setTailCall(boolean isTailCall) {
        LLVMSetTailCall(value, isTailCall ? 1 : 0);
    }

    /**
     * Add an incoming value to the end of a PHI list.
     */
    /*public void AddIncoming(Pointer<LLVMValueRef> IncomingValues,
            Pointer<LLVMBasicBlockRef > IncomingBlocks, int Count) {*/
    public void addIncoming(Value[] incomingValues,
            BasicBlock[] incomingBlocks, int count) {

        LLVMValueRef[] rawVals = new LLVMValueRef[incomingValues.length];
        for (int i = 0; i < incomingValues.length; i++) {
            rawVals[i] = incomingValues[i].value;
        }
        Pointer<LLVMValueRef> ptrVals = Pointer.pointerToArray(rawVals);

        LLVMBasicBlockRef[] rawBlocks = new LLVMBasicBlockRef[incomingBlocks.length];
        for (int i = 0; i < incomingBlocks.length; i++) {
            rawBlocks[i] = incomingBlocks[i].bb();
        }
        Pointer<LLVMBasicBlockRef> ptrBlocks = Pointer
                .pointerToArray(rawBlocks);

        LLVMAddIncoming(value, ptrVals, ptrBlocks, count);
    }

    /**
     * Obtain the number of incoming basic blocks to a PHI node.
     */
    public int countIncoming() {
        return LLVMCountIncoming(value);
    }

    /**
     * Obtain an incoming value to a PHI node as a LLVMValueRef.
     */
    public Value getIncomingValue(int index) {
        return new Value(LLVMGetIncomingValue(value, index));
    }

    /**
     * Obtain an incoming value to a PHI node as a LLVMBasicBlockRef.
     */
    public BasicBlock getIncomingBlock(int index) {
        return new BasicBlock(LLVMGetIncomingBlock(value, index));
    }

    static Pointer<LLVMValueRef> internalize(Value[] values) {
        int n = values.length;
        LLVMValueRef[] inner = new LLVMValueRef[n];
        for (int i = 0; i < n; i++) {
            inner[i] = values[i].value;
        }

        Pointer<LLVMValueRef> array = Pointer.allocateTypedPointers(
                LLVMValueRef.class, values.length);
        array.setArray(inner);

        return array;
    }

    public int getNumSuccessors() {
        assert isTerminatorInst();
	    return LLVMGetNumSuccessors(value);
    }

    public BasicBlock getSuccessor(int i) {
        assert isTerminatorInst();
        return new BasicBlock(LLVMGetSuccessor(value, i));
    }

}
