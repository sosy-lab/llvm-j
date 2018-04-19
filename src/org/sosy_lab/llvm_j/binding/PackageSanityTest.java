/*
 * llvm-j  is a library for parsing and modification of LLVM IR in Java.
 * This file is part of llvm-j.
 *
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

package org.sosy_lab.llvm_j.binding;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.testing.AbstractPackageSanityTests;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.sosy_lab.llvm_j.Module;

public class PackageSanityTest extends AbstractPackageSanityTests {
  {
    Path libraryPath = Paths.get("lib", "java", "runtime");
    List<Path> relevantLibDirs = ImmutableList.of(libraryPath);
    Module.addLibraryLookupPaths(relevantLibDirs);
    LLVMLibrary.instantiate();
    ignoreClasses(Predicates.equalTo(LLVMLibrary.class));
  }
}
