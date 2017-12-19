/*
 * Copyright (C) 2009 Olivier Chafik
 * Copyright (C) 2017 Dirk Beyer
 * All Rights Reserved.
 *
 * This file was originally a part of JNAerator (https://github.com/nativelibs4java/JNAerator).
 *
 * JNAerator is free software: you can redistribute it and/or modify
 * it and this file under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JNAerator and this file are distributed in the hope that they will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JNAerator/this project.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sosy_lab.llvm_j.binding.ext;

import com.sun.jna.ptr.ByReference;

public class NativeSizeByReference extends ByReference {

  public NativeSizeByReference() {
    this(new NativeSize(0L));
  }

  public NativeSizeByReference(NativeSize value) {
    super(NativeSize.SIZE);
    this.setValue(value);
  }

  public void setValue(NativeSize value) {
    if (NativeSize.SIZE == 4) {
      this.getPointer().setInt(0L, value.intValue());
    } else {
      if (NativeSize.SIZE != 8) {
        throw new AssertionError("GCCLong has to be either 4 or 8 bytes.");
      }

      this.getPointer().setLong(0L, value.longValue());
    }
  }

  public NativeSize getValue() {
    if (NativeSize.SIZE == 4) {
      return new NativeSize(this.getPointer().getInt(0L));
    } else if (NativeSize.SIZE == 8) {
      return new NativeSize(this.getPointer().getLong(0L));
    } else {
      throw new AssertionError("GCCLong has to be either 4 or 8 bytes.");
    }
  }
}
