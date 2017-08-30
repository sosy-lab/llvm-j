# LLVM-J

Wraps the C bindings of LLVM for use with Java using java native access ([JNA](https://github.com/java-native-access/jna)).

Uses [JNAerator](https://github.com/nativelibs4java/JNAerator) to generate
JNA bindings for the shared library of LLVM.

Proxy classes are provided in package `org.sosy_lab.llvm_j` for easy use.
The original bindings are located in `org.sosy_lab.llvm_j.binding`.

The proxy classes aim at parsing LLVM bitcode, not modifying it.
If you miss any functionality, we're always happy about pull-requests!
