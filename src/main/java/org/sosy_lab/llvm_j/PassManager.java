package org.sosy_lab.llvm_j;

import org.sosy_lab.llvm_j.binding.LLVMLibrary;

/** Pass manager. Always has to be disposed of with {@link #dispose()} to avoid memory leaks. */
public final class PassManager {

  private LLVMLibrary.LLVMPassManagerRef manager;

  LLVMLibrary.LLVMPassManagerRef manager() {
    return manager;
  }

  private PassManager(LLVMLibrary.LLVMPassManagerRef manager) {
    this.manager = manager;
  }

  /**
   * Constructs a new whole-module pass pipeline. This type of pipeline is suitable for link-time
   * optimization and whole-module transformations.
   */
  public static PassManager create() {
    return new PassManager(LLVMLibrary.LLVMCreatePassManager());
  }

  /**
   * Constructs a new function-by-function pass pipeline over the module provider. It does not take
   * ownership of the module provider. This type of pipeline is suitable for code generation and JIT
   * compilation tasks.
   */
  public static PassManager createForModule(Module m) {
    return new PassManager(LLVMLibrary.LLVMCreateFunctionPassManagerForModule(m.getModule()));
  }

  /** Deprecated: Use LLVMCreateFunctionPassManagerForModule instead. */
  public static PassManager createFPM(LLVMLibrary.LLVMModuleProviderRef mp) {
    return new PassManager(LLVMLibrary.LLVMCreateFunctionPassManager(mp));
  }

  /**
   * Finalizes all of the function passes scheduled in in the function pass manager. Returns 1 if
   * any of the passes modified the module, 0 otherwise. Frees the memory of a pass pipeline. For
   * function pipelines, does not free the module provider.
   */
  public void dispose() throws LLVMException {
    LLVMLibrary.LLVMBool successB = LLVMLibrary.LLVMFinalizeFunctionPassManager(manager);
    boolean success = Utils.llvmBoolToJavaBool(successB);
    LLVMLibrary.LLVMDisposePassManager(manager);
    manager = null;
    if (success) {
      throw new LLVMException("error in LLVMFinalizeFunctionPassManager");
    }
  }

  /* PassManager */
  // public static native int LLVMRunPassManager(LLVMPassManagerRef pm, LLVMModuleRef m);

  /**
   * Initializes all of the function passes scheduled in the function pass manager. Returns 1 if any
   * of the passes modified the module, 0 otherwise.
   */
  public void initialize() throws LLVMException {
    LLVMLibrary.LLVMBool errB = LLVMLibrary.LLVMInitializeFunctionPassManager(manager);
    boolean err = Utils.llvmBoolToJavaBool(errB);
    if (err) {
      throw new LLVMException("error in LLVMInitializeFunctionPassManager");
    }
  }

  /**
   * Initializes, executes on the provided module, and finalizes all of the passes scheduled in the
   * pass manager.
   *
   * @param m module to run on
   * @throws LLVMException if error occurs in underlying LLVM run pass manager.
   */
  public void runForModule(Module m) throws LLVMException {
    LLVMLibrary.LLVMBool errB = LLVMLibrary.LLVMRunPassManager(manager, m.getModule());
    boolean err = Utils.llvmBoolToJavaBool(errB);
    if (err) {
      throw new LLVMException("error in LLVMRunPassManager");
    }
  }

  /**
   * Executes all of the function passes scheduled in the function pass manager on the provided
   * function.
   *
   * @param f function to run on
   * @throws LLVMException if error occurs in underlying LLVM function pass manager.
   */
  public void runForFunction(Function f) throws LLVMException {
    LLVMLibrary.LLVMBool errB = LLVMLibrary.LLVMRunFunctionPassManager(manager, f.value());
    boolean err = Utils.llvmBoolToJavaBool(errB);
    if (err) {
      throw new LLVMException("error in LLVMRunFunctionPassManager");
    }
  }

  /* Function Pass Manager */
  public void addArgumentPromotionPass() {
    LLVMLibrary.LLVMAddArgumentPromotionPass(manager);
  }

  public void addConstantMergePass() {
    LLVMLibrary.LLVMAddConstantMergePass(manager);
  }

  public void addDeadArgEliminationPass() {
    LLVMLibrary.LLVMAddDeadArgEliminationPass(manager);
  }

  /*public void addDeadTypeEliminationPass() {
      LLVMAddDeadTypeEliminationPass(manager);
  }*/

  public void addFunctionAttrsPass() {
    LLVMLibrary.LLVMAddFunctionAttrsPass(manager);
  }

  public void addFunctionInliningPass() {
    LLVMLibrary.LLVMAddFunctionInliningPass(manager);
  }

  public void addGlobalDCEPass() {
    LLVMLibrary.LLVMAddGlobalDCEPass(manager);
  }

  public void addGlobalOptimizerPass() {
    LLVMLibrary.LLVMAddGlobalOptimizerPass(manager);
  }

  public void addIPConstantPropagationPass() {
    LLVMLibrary.LLVMAddIPConstantPropagationPass(manager);
  }

  /*public void addLowerSetJmpPass() {
      LLVMAddLowerSetJmpPass(manager);
  }*/

  public void addPruneEHPass() {
    LLVMLibrary.LLVMAddPruneEHPass(manager);
  }

  public void addIPSCCPPass() {
    LLVMLibrary.LLVMAddIPSCCPPass(manager);
  }

  public void addInternalizePass(boolean allButMain) {
    LLVMLibrary.LLVMAddInternalizePass(allButMain ? 1 : 0);
  }

  /*public void addRaiseAllocationsPass() {
      LLVMAddRaiseAllocationsPass(manager);
  }*/

  public void addStripDeadPrototypesPass() {
    LLVMLibrary.LLVMAddStripDeadPrototypesPass(manager);
  }

  public void addStripSymbolsPass() {
    LLVMLibrary.LLVMAddStripSymbolsPass(manager);
  }

  public void addAggressiveDCEPass() {
    LLVMLibrary.LLVMAddAggressiveDCEPass(manager);
  }

  public void addCFGSimplificationPass() {
    LLVMLibrary.LLVMAddCFGSimplificationPass(manager);
  }

  public void addDeadStoreEliminationPass() {
    LLVMLibrary.LLVMAddDeadStoreEliminationPass(manager);
  }

  public void addGVNPass() {
    LLVMLibrary.LLVMAddGVNPass(manager);
  }

  public void addIndVarSimplifyPass() {
    LLVMLibrary.LLVMAddIndVarSimplifyPass(manager);
  }

  public void addInstructionCombiningPass() {
    LLVMLibrary.LLVMAddInstructionCombiningPass(manager);
  }

  public void addJumpThreadingPass() {
    LLVMLibrary.LLVMAddJumpThreadingPass(manager);
  }

  public void addLICMPass() {
    LLVMLibrary.LLVMAddLICMPass(manager);
  }

  public void addLoopDeletionPass() {
    LLVMLibrary.LLVMAddLoopDeletionPass(manager);
  }

  public void addLoopRotatePass() {
    LLVMLibrary.LLVMAddLoopRotatePass(manager);
  }

  public void addLoopUnrollPass() {
    LLVMLibrary.LLVMAddLoopUnrollPass(manager);
  }

  public void addLoopUnswitchPass() {
    LLVMLibrary.LLVMAddLoopUnswitchPass(manager);
  }

  public void addMemCpyOptPass() {
    LLVMLibrary.LLVMAddMemCpyOptPass(manager);
  }

  public void addPromoteMemoryToRegisterPass() {
    LLVMLibrary.LLVMAddPromoteMemoryToRegisterPass(manager);
  }

  public void addReassociatePass() {
    LLVMLibrary.LLVMAddReassociatePass(manager);
  }

  public void addSCCPPass() {
    LLVMLibrary.LLVMAddSCCPPass(manager);
  }

  public void addScalarReplAggregatesPass() {
    LLVMLibrary.LLVMAddScalarReplAggregatesPass(manager);
  }

  public void addScalarReplAggregatesPassWithThreshold(int threshold) {
    LLVMLibrary.LLVMAddScalarReplAggregatesPassWithThreshold(manager, threshold);
  }

  public void addSimplifyLibCallsPass() {
    LLVMLibrary.LLVMAddSimplifyLibCallsPass(manager);
  }

  public void addTailCallEliminationPass() {
    LLVMLibrary.LLVMAddTailCallEliminationPass(manager);
  }

  public void addConstantPropagationPass() {
    LLVMLibrary.LLVMAddConstantPropagationPass(manager);
  }

  public void addDemoteMemoryToRegisterPass() {
    LLVMLibrary.LLVMAddDemoteMemoryToRegisterPass(manager);
  }

  public void addVerifierPass() {
    LLVMLibrary.LLVMAddVerifierPass(manager);
  }
}
