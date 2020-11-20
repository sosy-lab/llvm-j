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

import java.io.Closeable;
import java.io.IOException;
import org.sosy_lab.llvm_j.binding.LLVMLibrary;

/** Pass manager. Always has to be disposed of with {@link #close()} to avoid memory leaks. */
public final class PassManager implements Closeable {

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
    if (m == null) {
      throw new NullPointerException();
    }
    return new PassManager(LLVMLibrary.LLVMCreateFunctionPassManagerForModule(m.getModule()));
  }

  /** Deprecated: Use LLVMCreateFunctionPassManagerForModule instead. */
  public static PassManager createFPM(LLVMLibrary.LLVMModuleProviderRef mp) {
    checkNotNull(mp);
    return new PassManager(LLVMLibrary.LLVMCreateFunctionPassManager(mp));
  }

  /* PassManager */
  // public static native int LLVMRunPassManager(LLVMPassManagerRef pm, LLVMModuleRef m);

  /**
   * Initializes all of the function passes scheduled in the function pass manager.
   *
   * @throws LLVMException if an error occurs in the underlying LLVM pass manager during its
   *     initialization
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
   * @throws LLVMException if error occurs in the underlying LLVM run pass manager.
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
   * @throws LLVMException if an error occurs in the underlying LLVM function pass manager.
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

  @Override
  public void close() throws IOException {
    LLVMLibrary.LLVMBool successB = LLVMLibrary.LLVMFinalizeFunctionPassManager(manager);
    boolean success = Utils.llvmBoolToJavaBool(successB);
    LLVMLibrary.LLVMDisposePassManager(manager);
    manager = null;
    if (success) {
      throw new IOException(new LLVMException("error in LLVMFinalizeFunctionPassManager"));
    }
  }
}
