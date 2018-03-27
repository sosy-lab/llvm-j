/*
 * Copyright (C) 2009 Olivier Chafik
 * Copyright (C) 2017 Dirk Beyer
 * All Rights Reserved.
 *
 * This file was originally a part of JNAerator (https://github.com/nativelibs4java/JNAerator).
 *
 * JNAerator is free software: you can redistribute it and/or modify
 * it and this file under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JNAerator and this file are distributed in the hope that they will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License
 * along with JNAerator/this project.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sosy_lab.llvm_j.binding.ext;

import com.sun.jna.IntegerType;
import com.sun.jna.Native;

public class NativeSize extends IntegerType {
  private static final long serialVersionUID = 2398288011955445078L;
  public static final int SIZE = Native.SIZE_T_SIZE;

  public NativeSize() {
    this(0L);
  }

  public NativeSize(long value) {
    super(SIZE, value);
  }
}
