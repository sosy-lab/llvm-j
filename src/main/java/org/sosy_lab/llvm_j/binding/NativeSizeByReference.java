package org.sosy_lab.llvm_j.binding;

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
                throw new RuntimeException("GCCLong has to be either 4 or 8 bytes.");
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
            throw new RuntimeException("GCCLong has to be either 4 or 8 bytes.");
        }
    }
}
