#!/bin/bash

set -euo pipefail

function download_and_extract {

    TMP_PACKAGE=Packages
    if [[ -f "$TMP_PACKAGE" ]]; then
        >&2 echo "$TMP_PACKAGE already exists. Moved to $TMP_PACKAGE.old"
        mv "$TMP_PACKAGE" "$TMP_PACKAGE.old"
    fi
    set +e
    wget http://apt.llvm.org/$UBUNTU_VERSION/dists/llvm-toolchain-$UBUNTU_VERSION-${LLVM_VERSION}/main/binary-amd64/Packages.gz -O "$TMP_PACKAGE".gz
    RESULT=$?
    set -e
    if [ $RESULT -eq 0 ]; then
      gunzip "$TMP_PACKAGE".gz
      CANDIDATE_LINES=`grep "Filename:" "$TMP_PACKAGE" | grep libllvm${LLVM_VERSION}_`

      if [[ `echo $CANDIDATE_LINES | wc -l` -ne 1 ]]; then
        >&2 echo "Error: Not exactly one possible candidate. Candidates:"
        >&2 echo $CANDIDATE_LINES
        exit 1
      fi

      DEB_SUFFIX=`echo $CANDIDATE_LINES | cut -d":" -f2 | tr -d " "`
      DEB_NAME="$(echo $DEB_SUFFIX | rev | cut -d"/" -f1 | rev)"

      # Download deb if it doesn't exist yet
      if [[ ! -e $DEB_NAME ]]; then
          DEB_URL="http://apt.llvm.org/$UBUNTU_VERSION/${DEB_SUFFIX}"
          if wget $DEB_URL -O "$DEB_NAME"; then
            echo "Download successful"
          else
            >&2 echo "Error: wget failed (see above)."
            exit 2
          fi
      fi
    else
      apt-get download libllvm$LLVM_VERSION
      DEB_NAME=$(find . -name "libllvm$LLVM_VERSION*.deb" | head -n 1)
    fi

    DATA_TAR=$(ar t "$DEB_NAME"| grep data.tar)
    TMP_DATA_TAR="$TMP_LLVM_FOLDER/$DATA_TAR"
    (cd $TMP_LLVM_FOLDER; dpkg-deb -x $DEB_NAME . && mv $(find . -type f -name 'libLLVM*.so*') .)

    LIB_FILE=`find "$TMP_LLVM_FOLDER" -maxdepth 1 -name 'libLLVM*.so*' -type f`
    if [[ `echo $LIB_FILE | wc -l` -ne 1 ]]; then
      >&2 echo "Error: Not exactly one library available."
      >&2 echo "Candidates:"
      >&2 echo $LIB_FILE
      exit 3
    fi
}

# Download the LLVM shared library of the version number
# given on the command line.

TMP=${TMP:-/tmp/llvm-j-$RANDOM}
if [[ ! -d "$TMP" ]]; then
    mkdir -p "$TMP"
fi

TMP_LLVM_FOLDER="$TMP"
TMP_DEPS_FOLDER=$TMP/deps

LLVM_FULL_VERSION=$1
if [[ -z "$LLVM_FULL_VERSION" ]]; then
  >&2 echo "Usage: ./download_lib.sh LLVM_VERSION UBUNTU_VERSION"
  exit 2
fi
UBUNTU_VERSION=$2
if [[ -z "$UBUNTU_VERSION" ]]; then
  >&2 echo "Usage: ./download_lib.sh LLVM_VERSION UBUNTU_VERSION"
  exit 2
fi

# Cut minor revisions
LLVM_VERSION=`echo $LLVM_FULL_VERSION | cut -d'.' -f1,2`

if ! hash patchelf 2> /dev/null; then
    >&2 echo "Application patchelf required but not installed."
    exit 6
fi

(cd $TMP && download_and_extract "$@")

LIB_FILE=`find "$TMP_LLVM_FOLDER" -maxdepth 1 -name 'libLLVM*.so*' -type f`

# change rpath of libLLVM to take our libtinfo and libedit without
# the need to set LD_LIBRARY_PATH whenever libLLVM should be used
patchelf --set-rpath '$ORIGIN' "$LIB_FILE"


EXPECTED_LIB="libLLVM-${LLVM_FULL_VERSION}.so"
CMD="cp ${LIB_FILE} ${EXPECTED_LIB}"
echo $CMD
$CMD

echo "libLLVM-${LLVM_FULL_VERSION}.so extracted successfully."
