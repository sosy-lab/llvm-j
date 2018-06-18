/*
 * llvm-j  is a library for parsing and modification of LLVM IR in Java.
 * This file is part of llvm-j.
 *
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

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ModuleTest {

  private Context context;

  @Before
  public void setUp_library() {
    Path libraryPath = Paths.get("lib", "java", "runtime");
    List<Path> relevantLibDirs = ImmutableList.of(libraryPath);
    Module.addLibraryLookupPaths(relevantLibDirs);
    context = Context.create();
  }

  @After
  public void tearDown_context() {
    context.close();
  }

  @Test
  @SuppressWarnings("deprecation")
  public void test_parseBitcode_noContext_valid() throws LLVMException {
    Path llvmFile = Paths.get("build", "test.bc");

    try (Module m = Module.parseIR(llvmFile)) {
      expectComponentsExist(m);
    }
  }

  @Test
  @SuppressWarnings("deprecation")
  public void test_parseLl_noContext_valid() throws LLVMException {
    Path llvmFile = Paths.get("build", "test.ll");

    try (Module m = Module.parseIR(llvmFile)) {
      expectComponentsExist(m);
    }
  }

  @Test
  public void test_parseBitcode_withContext_valid() throws LLVMException {
    Path llvmFile = Paths.get("build", "test.bc");

    try (Module m = Module.parseIR(llvmFile, context)) {
      expectComponentsExist(m);
    }
  }

  @Test
  public void test_parseLl_withContext_valid() throws LLVMException {
    Path llvmFile = Paths.get("build", "test.ll");

    try (Module m = Module.parseIR(llvmFile, context)) {
      expectComponentsExist(m);
    }
  }

  @Test
  public void test_parseString_valid() throws LLVMException {
     /*String llvmCode = "; ModuleID = 'test.bc'\n" +
        "source_filename = \"test.c\"\n" +
        "target datalayout = \"e-m:e-i64:64-f80:128-n8:16:32:64-S128\"\n" +
        "target triple = \"x86_64-unknown-linux-gnu\"\n" +
        "\n" +
        "; Function Attrs: nounwind uwtable\n" +
        "define i32 @main() #0 {\n" +
        "  %1 = alloca i32, align 4\n" +
        "  %2 = alloca i32, align 4\n" +
        "  store i32 0, i32* %1, align 4\n" +
        "  store i32 0, i32* %2, align 4\n" +
        "  %3 = load i32, i32* %2, align 4\n" +
        "  %4 = icmp sgt i32 %3, 0\n" +
        "  br i1 %4, label %5, label %6\n" +
        "\n" +
        "; <label>:5:                                      ; preds = %0\n" +
        "  store i32 -1, i32* %1, align 4\n" +
        "  br label %7\n" +
        "\n" +
        "; <label>:6:                                      ; preds = %0\n" +
        "  store i32 0, i32* %1, align 4\n" +
        "  br label %7\n" +
        "\n" +
        "; <label>:7:                                      ; preds = %6, %5\n" +
        "  %8 = load i32, i32* %1, align 4\n" +
        "  ret i32 %8\n" +
        "}\n" +
        "\n" +
        "attributes #0 = { nounwind uwtable \"disable-tail-calls\"=\"false\" \"less-precise-fpmad\"=\"false\" \"no-frame-pointer-elim\"=\"true\" \"no-frame-pointer-elim-non-leaf\" \"no-infs-fp-math\"=\"false\" \"no-jump-tables\"=\"false\" \"no-nans-fp-math\"=\"false\" \"no-signed-zeros-fp-math\"=\"false\" \"stack-protector-buffer-size\"=\"8\" \"target-cpu\"=\"x86-64\" \"target-features\"=\"+fxsr,+mmx,+sse,+sse2,+x87\" \"unsafe-fp-math\"=\"false\" \"use-soft-float\"=\"false\" }\n" +
        "\n" +
        "!llvm.ident = !{!0}\n" +
        "\n" +
        "!0 = !{!\"clang version 3.9.1 (tags/RELEASE_391/final)\"}\n";*/
    String llvmCode = "define i32 main() #0 { %0 = alloca i32, align4\n ret i32 %0\n}\n";

    try (Module m = Module.parseIRString(llvmCode, context)) {
      expectComponentsExist(m);
    }
  }

  /** Check that basic components of the provided {@link Module} exist. */
  private static void expectComponentsExist(Module pModule) {
    assertThat(pModule).isNotNull();

    Value firstFunction = pModule.getFirstFunction();
    assertThat(firstFunction).isNotNull();

    BasicBlock firstBlock = firstFunction.getFirstBasicBlock();
    assertThat(firstBlock).isNotNull();

    Value firstInstruction = firstBlock.getFirstInstruction();
    assertThat(firstInstruction).isNotNull();
  }
}
