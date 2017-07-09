package org.llvm.binding;

import com.sun.jna.IntegerType;
import com.sun.jna.Native;

public class NativeSize extends IntegerType {
    private static final long serialVersionUID = 2398288011955445078L;
    public static int SIZE = Native.SIZE_T_SIZE;

    public NativeSize() {
        this(0L);
    }

    public NativeSize(long value) {
        super(SIZE, value);
    }
}