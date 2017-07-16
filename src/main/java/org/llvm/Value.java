package org.llvm;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.llvm.binding.LLVMLibrary.*;

/**
 * Represents an individual value in LLVM IR.
 */
public class Value {

    public enum OpCode {
        Ret(1),
        Br(2),
        Switch(3),
        IndirectBr(4),
        Invoke(5),
        Unreachable(7),
        Add(8),
        FAdd(9),
        Sub(10),
        FSub(11),
        Mul(12),
        FMul(13),
        UDiv(14),
        SDiv(15),
        FDiv(16),
        URem(17),
        SRem(18),
        FRem(19),
        Shl(20),
        LShr(21),
        AShr(22),
        And(23),
        Or(24),
        Xor(25),
        Alloca(26),
        Load(27),
        Store(28),
        GetElementPtr(29),
        Trunc(30),
        ZExt(31),
        SExt(32),
        FPToUI(33),
        FPToSI(34),
        UIToFP(35),
        SIToFP(36),
        FPTrunc(37),
        FPExt(38),
        PtrToInt(39),
        IntToPtr(40),
        BitCast(41),
        AddrSpaceCast(60),
        ICmp(42),
        FCmp(43),
        PHI(44),
        Call(45),
        Select(46),
        UserOp1(47),
        UserOp2(48),
        VAArg(49),
        ExtractElement(50),
        InsertElement(51),
        ShuffleVector(52),
        ExtractValue(53),
        InsertValue(54),
        Fence(55),
        AtomicCmpXchg(56),
        AtomicRMW(57),
        Resume(58),
        LandingPad(59),
        CleanupRet(61),
        CatchRet(62),
        CatchPad(63),
        CleanupPad(64),
        CatchSwitch(65);

        private final int value;
        OpCode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum CallConv {
        CCallConv(0),
        FastCallConv(8),
        ColdCallConv(9),
        WebKitJSCallConv(12),
        AnyRegCallConv(13),
        X86StdcallCallConv(64),
        X86FastcallCallConv(65);

        private final int value;
        CallConv(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum IntPredicate {
        /** < equal */
        IntEQ(32),
        /** < not equal */
        IntNE(33),
        /** < unsigned greater than */
        IntUGT(34),
        /** < unsigned greater or equal */
        IntUGE(35),
        /** < unsigned less than */
        IntULT(36),
        /** < unsigned less or equal */
        IntULE(37),
        /** < signed greater than */
        IntSGT(38),
        /** < signed greater or equal */
        IntSGE(39),
        /** < signed less than */
        IntSLT(40),
        /** < signed less or equal */
        IntSLE(41);

        private final int value;
        IntPredicate(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum Linkage {
        /** < Externally visible function */
        ExternalLinkage(0),
        AvailableExternallyLinkage(1),
        /** < Keep one copy of function when linking (inline) */
        LinkOnceAnyLinkage(2),
        /**
         * < Same, but only replaced by something<br>
         * equivalent.
         */
        LinkOnceODRLinkage(3),
        /** < Obsolete */
        LinkOnceODRAutoHideLinkage(4),
        /** < Keep one copy of function when linking (weak) */
        WeakAnyLinkage(5),
        /**
         * < Same, but only replaced by something<br>
         * equivalent.
         */
        WeakODRLinkage(6),
        /** < Special purpose, only applies to global arrays */
        AppendingLinkage(7),
        /**
         * < Rename collisions when linking (static<br>
         * functions)
         */
        InternalLinkage(8),
        /** < Like Internal, but omit from symbol table */
        PrivateLinkage(9),
        /** < Obsolete */
        DLLImportLinkage(10),
        /** < Obsolete */
        DLLExportLinkage(11),
        /** < ExternalWeak linkage description */
        ExternalWeakLinkage(12),
        /** < Obsolete */
        GhostLinkage(13),
        /** < Tentative definitions */
        CommonLinkage(14),
        /** < Like Private, but linker removes. */
        LinkerPrivateLinkage(15),
        /** < Like LinkerPrivate, but is weak. */
        LinkerPrivateWeakLinkage(16);

        private final int value;
        Linkage(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum Visibility {
        /** < The GV is visible */
        Default(0),
        /** < The GV is hidden */
        Hidden(1),
        /** < The GV is protected */
        Protected(2);

        private final int value;
        Visibility(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private LLVMValueRef value;

    LLVMValueRef value() {
        return value;
    }

    @Override
    public boolean equals(final Object pObj) {
        if (!(pObj instanceof Value)) {
            return false;
        }
        Value rhs = (Value) pObj;
        if (value == null) {
            return rhs.value == null;

        } else {
            return value.getPointer().equals(rhs.value.getPointer());
        }
    }

    @Override
    public int hashCode() {
        return value.hashCode();
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

    public long getAddress() {
        if (value == null) {
            return 0;
        } else {
            return Pointer.nativeValue(value.getPointer());
        }
    }

    /**
     * Cast this value into a Function
     */
    public Function asFunction() {
        assert isFunction();
        return new Function(value);
    }

    /**
     * Obtain the string name of a value.<br>
     *
     * @see llvm::Value::getName()
     */
    public String getValueName() {
        return LLVMGetValueName(value);
    }

    /**
     * Dump a representation of a value to stderr.<br>
     *
     * @see llvm::Value::dump()
     */
    public void dumpValue() {
        LLVMDumpValue(value);
    }

    @Override
    public String toString() {
	  Pointer ret = LLVMPrintValueToString(value);
      return ret.getString(Native.getNativeSize(String.class));
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
        try {
            return LLVMIsAArgument(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isABasicBlock() {
        try {
            return LLVMIsABasicBlock(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isInlineAsm() {
        try {
            return LLVMIsAInlineAsm(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isUser() {
        try {
            return LLVMIsAUser(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isAConstant() {
        try {
            return LLVMIsAConstant(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isConstantAggregateZero() {
        try {
            return LLVMIsAConstantAggregateZero(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isConstantArray() {
        try {
            return LLVMIsAConstantArray(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isConstantExpr() {
        try {
            return LLVMIsAConstantExpr(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isConstantFP() {
        try {
            return LLVMIsAConstantFP(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isConstantInt() {
        try {
            return LLVMIsAConstantInt(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isConstantPointerNull() {
        try {
            return LLVMIsAConstantPointerNull(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isConstantStruct() {
        try {
            return LLVMIsAConstantStruct(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isConstantVector() {
        try {
            return LLVMIsAConstantVector(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isGlobalValue() {
        try {
            return LLVMIsAGlobalValue(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isFunction() {
        try {
            return LLVMIsAFunction(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isGlobalAlias() {
        try {
            return LLVMIsAGlobalAlias(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isGlobalVariable() {
        try {
            return LLVMIsAGlobalVariable(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isUndefValue() {
        try {
            return LLVMIsAUndefValue(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isInstruction() {
        try {
            return LLVMIsAInstruction(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isBinaryOperator() {
        try {
            return LLVMIsABinaryOperator(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isCallInst() {
        try {
            return LLVMIsACallInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isIntrinsicInst() {
        try {
            return LLVMIsAIntrinsicInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isDbgInfoIntrinsic() {
        try {
            return LLVMIsADbgInfoIntrinsic(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isDbgDeclareInst() {
        try {
            return LLVMIsADbgDeclareInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    //	public boolean isEHSelectorInst() { return LLVMIsAEHSelectorInst(value) != null; }
    public boolean isMemIntrinsic() {
        try {
            return LLVMIsAMemIntrinsic(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isMemCpyInst() {
        try {
            return LLVMIsAMemCpyInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isMemMoveInst() {
        try {
            return LLVMIsAMemMoveInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isMemSetInst() {
        try {
            return LLVMIsAMemSetInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isCmpInst() {
        try {
            return LLVMIsACmpInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isFCmpInst() {
        try {
            return LLVMIsAFCmpInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isICmpInst() {
        try {
            return LLVMIsAICmpInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isExtractElementInst() {
        try {
            return LLVMIsAExtractElementInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isGetElementPtrInst() {
        try {
            return LLVMIsAGetElementPtrInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isInsertElementInst() {
        try {
            return LLVMIsAInsertElementInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isInsertValueInst() {
        try {
            return LLVMIsAInsertValueInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isPHINode() {
        try {
            return LLVMIsAPHINode(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isSelectInst() {
        try {
            return LLVMIsASelectInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isShuffleVectorInst() {
        try {
            return LLVMIsAShuffleVectorInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isStoreInst() {
        try {
            return LLVMIsAStoreInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isTerminatorInst() {
        try {
            return LLVMIsATerminatorInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isBranchInst() {
        try {
            return LLVMIsABranchInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isIndirectBranchInst() {
        try {
            return LLVMIsAIndirectBrInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isInvokeInst() {
        try {
            return LLVMIsAInvokeInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isReturnInst() {
        try {
            return LLVMIsAReturnInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public Value getReturnValue() {
        assert isReturnInst();

        if (getNumOperands() > 0) {
            return getOperand(0);
        } else {
            return null;
        }
    }

    public boolean isSwitchInst() {
        try {
            return LLVMIsASwitchInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isUnreachableInst() {
        try {
            return LLVMIsAUnreachableInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    //	public boolean isUnwindInst() { return LLVMIsAUnwindInst(value) != null; }
    public boolean isUnaryInstruction() {
        try {
            return LLVMIsAUnaryInstruction(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isAllocaInst() {
        try {
            return LLVMIsAAllocaInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public TypeRef getAllocatedType() {
        return new TypeRef(LLVMGetAllocatedType(value));
    }

    public boolean isCastInst() {
        try {
            return LLVMIsACastInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isBitCastInst() {
        try {
            return LLVMIsABitCastInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isFPExtInst() {
        try {
            return LLVMIsAFPExtInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isFPToSIInst() {
        try {
            return LLVMIsAFPToSIInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isFPToUIInst() {
        try {
            return LLVMIsAFPToUIInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isFPTruncInst() {
        try {
            return LLVMIsAFPTruncInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isIntToPtrInst() {
        try {
            return LLVMIsAIntToPtrInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isPtrToIntInst() {
        try {
            return LLVMIsAPtrToIntInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isSExtInst() {
        try {
            return LLVMIsASExtInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isSIToFPInst() {
        try {
            return LLVMIsASIToFPInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isTruncInst() {
        try {
            return LLVMIsATruncInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isUIToFPInst() {
        try {
            return LLVMIsAUIToFPInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isZExtInst() {
        try {
            return LLVMIsAZExtInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isExtractValueInst() {
        try {
            return LLVMIsAExtractValueInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isLoadInst() {
        try {
            return LLVMIsALoadInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isVAArgInst() {
        try {
            return LLVMIsAVAArgInst(value) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Determine whether the specified constant instance is constant.
     */
    public boolean isConstant() {
        LLVMBool b = LLVMIsConstant(value);
        return Utils.llvmBoolToJavaBool(b);
    }

    /**
     * Determine whether a value instance is null.<br>
     *
     * @see llvm::Constant::isNullValue()
     */
    public boolean isNull() {
        LLVMBool b = LLVMIsNull(value);
        return Utils.llvmBoolToJavaBool(b);
    }

    /**
     * Determine whether a value instance is undefined.
     */
    public boolean isUndef() {
        LLVMBool b = LLVMIsUndef(value);
        return Utils.llvmBoolToJavaBool(b);
    }

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

    /*public ValueRef constStructInContext(Context c, ValueRef[] constantVals,
            int count, boolean packed) {
        return new ValueRef(LLVMConstStructInContext(C.context(), constantVals,
                count, packed ? 1 : 0));
    }*/

    public OpCode getOpCode() {
        int opcode = LLVMGetInstructionOpcode(value);

        // Use the value-based enums with this for-loop
        // to convert the integer opcode returned by the llvm library
        // into an enum without an if-else statement for every
        // single possible value
        for (OpCode code : OpCode.values()) {
            if (code.getValue() == opcode) {
                return code;
            }
        }
        throw new AssertionError("Unhandled code id " + opcode);
    }

    public IntPredicate getICmpPredicate() {
	    int code = LLVMGetICmpPredicate(value);

        for (IntPredicate predicate : IntPredicate.values()) {
            if (code == predicate.getValue()) {
                return predicate;
            }
        }
        throw new AssertionError("Unhandled code id " + code);
    }

    public boolean isConditional() {
        LLVMBool b = LLVMIsConditional(value);
        return Utils.llvmBoolToJavaBool(b);
    }

    public Value getCondition() {
        assert isConditional();
        return new Value(LLVMGetCondition(value));
    }

    public static Value blockAddress(Value f, BasicBlock bb) {
        return new Value(LLVMBlockAddress(f.value(), bb.bb()));
    }

    public Module getGlobalParent() {
        return new Module(LLVMGetGlobalParent(value));
    }

    public boolean isDeclaration() {
        if (value == null) {
            throw new NullPointerException("Null pointer value");
        }
        LLVMBool b = LLVMIsDeclaration(value);
        return Utils.llvmBoolToJavaBool(b);
    }

    public Linkage getLinkage() {
        int code = LLVMGetLinkage(value);

        for (Linkage l : Linkage.values()) {
            if (code == l.getValue()) {
                return l;
            }
        }
        throw new AssertionError("Unhandled code id " + code);
    }

    public String getSection() {
        return LLVMGetSection(value);
    }

    public Visibility getVisibility() {
        int code = LLVMGetVisibility(value);

        for (Visibility v : Visibility.values()) {
            if (code == v.getValue()) {
                return v;
            }
        }
        throw new AssertionError("Unhandled code id " + code);
    }

    public int getAlignment() {
        return LLVMGetAlignment(value);
    }

    // this.value is GlobalVar
    public Value getNextGlobal() {
        return new Value(LLVMGetNextGlobal(value));
    }

    public Value getPreviousGlobal() {
        return new Value(LLVMGetPreviousGlobal(value));
    }

    public Value getInitializer() {
        return new Value(LLVMGetInitializer(value));
    }

    public boolean isExternallyInitialized() {
        LLVMBool b = LLVMIsExternallyInitialized(value);
        return Utils.llvmBoolToJavaBool(b);
    }

    public boolean isThreadLocal() {
        LLVMBool b = LLVMIsThreadLocal(value);
        return Utils.llvmBoolToJavaBool(b);
    }

    public boolean isGlobalConstant() {
        LLVMBool b = LLVMIsGlobalConstant(value);
        return Utils.llvmBoolToJavaBool(b);
    }

    /**
     * Advance a Function iterator to the next Function.<br>
     * Returns null if the iterator was already at the end and there are no more<br>
     * functions.
     */
    public Value getNextFunction() {
        LLVMValueRef nextFunc = LLVMGetNextFunction(value);
        if (nextFunc == null) {
            return null;
        } else {
            return new Value(nextFunc);
        }
    }

    /**
     * Decrement a Function iterator to the previous Function.<br>
     * Returns null if the iterator was already at the beginning and there are<br>
     * no previous functions.
     */
    public Value getPreviousFunction() {
        LLVMValueRef previousFunc = LLVMGetPreviousFunction(value);
        if (previousFunc == null) {
            return null;
        } else {
            return new Value(previousFunc);
        }
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
    public CallConv getFunctionCallConv() {
        int code = LLVMGetFunctionCallConv(value);

        for (CallConv v : CallConv.values()) {
            if (code == v.getValue()) {
                return v;
            }
        }
        throw new AssertionError("Unhandled code id " + code);
    }

    /**
     * Obtain the name of the garbage collector to use during code<br>
     * generation.<br>
     *
     * @see llvm::Function::getGC()
     */
    public String getGC() {
        return LLVMGetGC(value);
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
        List<Value> params = new ArrayList<Value>(paramCount);

        if (paramCount > 0) {
            int valueRefOffset = Native.getNativeSize(LLVMValueRef.class);
            Memory arrayPointer = new Memory(paramCount * valueRefOffset);
            LLVMValueRef paramArray = new LLVMValueRef(arrayPointer);
            LLVMGetParams(value, paramArray);

            Pointer[] paramRefs = new Pointer[paramCount];
            arrayPointer.read(valueRefOffset, paramRefs, 0, paramCount);
            for (int i = 0; i < paramCount; i++) {
                LLVMValueRef valueRef = new LLVMValueRef(paramRefs[i]);
                params.add(new Value(valueRef));
            }
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
     * Determine whether a LLVMValueRef is itself a basic block.
     */
    public boolean isBasicBlock() {
        LLVMBool b = LLVMValueIsBasicBlock(value);
        return Utils.llvmBoolToJavaBool(b);
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
    public List<BasicBlock> getBasicBlocks() {
        int blockCount = countBasicBlocks();
        List<BasicBlock> blocks = new ArrayList<BasicBlock>(blockCount);

        if (blockCount > 0) {
            int blockRefOffset = Native.getNativeSize(LLVMBasicBlockRef.class);
            Memory arrayPointer = new Memory(blockCount * blockRefOffset);
            LLVMBasicBlockRef blockArray = new LLVMBasicBlockRef(arrayPointer);
            LLVMGetBasicBlocks(value, blockArray);

            Pointer[] blockRefs = new Pointer[blockCount];
            arrayPointer.read(blockRefOffset, blockRefs, 0, blockCount);
            for (int i = 0; i < blockCount; i++) {
                LLVMBasicBlockRef blockRef = new LLVMBasicBlockRef(blockRefs[i]);
                blocks.add(new BasicBlock(blockRef));
            }
        }

        return blocks;
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
        LLVMBool b = LLVMIsTailCall(value);
        return Utils.llvmBoolToJavaBool(b);
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

    public int getNumSuccessors() {
        assert isTerminatorInst();
	    return LLVMGetNumSuccessors(value);
    }

    public BasicBlock getSuccessor(int i) {
        assert isTerminatorInst();
        return new BasicBlock(LLVMGetSuccessor(value, i));
    }

}
