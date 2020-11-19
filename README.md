# llvm-j

llvm-j is a Java library for parsing and modifying LLVM IR.

The goals of llvm-j are:

  1. Provide an easy to use LLVM IR parser that feels like a native Java
      library (compared to a collection of JNI bindings).
  2. Make it easy to upgrade to higher versions of LLVM without API changes.
      This is important since LLVM updates may change the LLVM core API -
      we want our users to be able to use llvm-j independent of such changes,
      and allow them to use new LLVM versions without a need to update their code.

To achieve these goals, llvm-j uses Java bindings for the original LLVM C parsing library with java native access ([JNA](https://github.com/java-native-access/jna)),
but provides proxy classes for the most common parsing tasks.
This way, the user never has to call the C bindings directly,
but can work with our own, Java-like API (e.g., we aim to throw Exceptions
for illegal states instead of returning values with a special error meaning).

The Java bindings for the LLVM C parsing library are automatically
generated with [JNAerator](https://github.com/nativelibs4java/JNAerator).
Through this, it is easy to update the bindings to new versions of LLVM,
and the user doesn't have to change any code since the llvm-j API stays
the same.

Currently, we use LLVM **6.0**.

## Use

The proxy classes are provided in Java package [`org.sosy_lab.llvm_j`](ADDLINK) for easy use.
The original bindings are located in [`org.sosy_lab.llvm_j.binding`](ADDLINK).

If you didn't put the LLVM 6.0 shared library in one of your system directories
for library lookup,
you can tell llvm-j in which directory the library is in in two ways:

    1. Provide system property `jna.library.path` to Java, for example
       `java -Djna.library.path=additional/lookup/path -jar appParsingLlvm.jar`
       or
    2. Add the directory in the code
       with static method [`Module#addLibraryLookupPaths(List<Path>)`](ADDLINK),
       *before* calling `Module#parseIR(String)`.

To parse LLVM IR, call static method [`Module#parseIR(String)`](ADDLINK) with
the file to parse as argument.
Currently, llvm-j only understands LLVM IR in **bitcode format**
(usually file suffix *.bc).
Method `Module#parseIR(String)` will return a [`Module`](ADDLINK) object

## Development


To build this project yourself, you should first clone or download
this repo.  
The project consists of three components:

  1. The automatically generated Java bindings of the LLVM library
  2. The Java API
  3. The shared library of LLVM

We use `ant` to manage our build process.
If it is not yet installed on your system, you will have to do so.

### Generating Bindings

Generated bindings are already provided and part of the project.
Thus, generating them manually is usually not necessary.

If you still want to re-generate the bindings,
you require an installation of LLVM that includes the LLVM C headers.
If `/path/to/llvm` includes the LLVM C-headers directory `/path/to/llvm/includes/llvm-c`, run:

```
    ant bindings -Dllvm.home=/path/to/llvm
```

Notice that new bindings may require modifications to the proxy classes,
since the LLVM C API may change over time.

### Build Java API
To compile the Java API, run `ant` in the project's root directory.  
To create a jar file for distribution, run `ant jar`.  
The output of the command will tell you the name of the created jar file.

The jar file does **not** include the shared libraries of LLVM.

### Get Shared Library of LLVM
llvm-j dynamically loads the shared libraries of LLVM, so they must be available
for your machine in the right version.
For convenience, we provide a mechanism to automatically download and extract
the shared libraries from an Ubuntu package (for 64 bit systems).
These libraries should work on most Linux distributions.

#### Library Download
**Requirements:**
We assume that you have common command-line tools installed.  
In addition, tool `chrpath` is required.
On Ubuntu, it can be installed by running
    `sudo apt install chrpath`.

**Download:**
To automatically download the library and its dependencies, execute on the command-line,
from the project's root directory:

```
    ant download-library
```

The libraries will be automatically downloaded and put into directory
`lib/native`.
You can then either move them to a system path for libraries (e.g., `/usr/local/lib`) or put them in your project.

#### Others ways to get the library
If you use Windows, macOS, or the provided libraries do not work on your system,
you can

  1) use an [LLVM package][1] that includes the shared libraries for your system
or
  2) [compile the shared libraries][2] yourself.

For both options, make sure that you use the correct version of LLVM.

[1]: http://releases.llvm.org/download.html
[2]: https://releases.llvm.org/6.0.0/docs/CMake.html

### Creating JavaDoc

To create the JavaDoc, run `ant javadoc` in the project's root directory.

### Tools for Code Quality

We provide some checks that may help you in writing good and correct code.
You can run: 
  * [CheckStyle](http://checkstyle.sourceforge.net/)
      through `ant checkstyle`
  * [Eclipse Compiler](https://www.eclipse.org/jdt/)
      with Eclipse-specific warnings
      through `ant build-project-ecj`
  * [Google Code Formatter](https://github.com/google/google-java-format)
      through `ant format-source`
  * [SpotBugs](https://github.com/spotbugs/spotbugs)
      through `ant spotbugs`

You can run `ant all-checks` to run CheckStyle, the Eclipse compiler, the JavaDoc
task (which performs linting for JavaDoc) and SpotBugs.

These tools are, of course, not enough to ensure good code quality, but only some
helpers.


Currently, the proxy classes aim at parsing LLVM IR bitcode, not modifying it.
If you miss any functionality, please create a new issue or message us
to help us improve llvm-j!
