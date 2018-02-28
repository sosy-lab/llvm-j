#!/bin/bash

# Download the LLVM shared library of the version number
# given on the command line.
# We use packages prepared for Ubuntu 12.04 to ensure compatibility
# with older systems.

LLVM_FULL_VERSION=$1
if [[ -z $LLVM_FULL_VERSION ]]; then
  >&2 echo "Usage: ./download_lib.sh LLVM_VERSION"
  exit 10
fi

EXPECTED_LIB="libLLVM-${LLVM_FULL_VERSION}.so"
if [[ -e $EXPECTED_LIB ]]; then
  >&2 echo "$EXPECTED_LIB already exists. Stored as bck/${EXPECTED_LIB}"
  mkdir -p bck
  mv $EXPECTED_LIB bck/${EXPECTED_LIB}
fi

# Cut minor revisions
LLVM_VERSION=`echo $LLVM_FULL_VERSION | cut -d'.' -f1,2`
TMP=/tmp/llvm-j-$RANDOM
if [[ ! -d "$TMP" ]]; then
    mkdir -p "$TMP"
fi
TMP_PACKAGE=$TMP/Packages
if [[ -f "$TMP_PACKAGE" ]]; then
    >&2 echo "$TMP_PACKAGE already exists. Moved to $TMP_PACKAGE.old"
    mv "$TMP_PACKAGE" "$TMP_PACKAGE.old"
fi
wget -N http://apt.llvm.org/precise/dists/llvm-toolchain-precise-${LLVM_VERSION}/main/binary-amd64/Packages.gz -O "$TMP_PACKAGE".gz
gunzip "$TMP_PACKAGE".gz
CANDIDATE_LINES=`grep "Filename:" "$TMP_PACKAGE" | grep libllvm${LLVM_VERSION}_`

if [[ `echo $CANDIDATE_LINES | wc -l` -ne 1 ]]; then
  >&2 echo "Error: Not exactly one possible candidate. Candidates:"
  >&2 echo $CANDIDATE_LINES
  exit 1
fi

DEB_SUFFIX=`echo $CANDIDATE_LINES | cut -d":" -f2 | tr -d " "`
DEB_NAME="$TMP/$(echo $DEB_SUFFIX | rev | cut -d"/" -f1 | rev)"

# Download deb if it doesn't exist yet
if [[ ! -e $DEB_NAME ]]; then
    DEB_URL="http://apt.llvm.org/precise/${DEB_SUFFIX}"
    if wget $DEB_URL -O "$DEB_NAME"; then
      echo "Download successful"
    else
      >&2 echo "Error: wget failed (see above)."
      exit 2
    fi
fi

DATA_TAR="data.tar.gz"
TMP_UNTAR_FOLDER="$TMP"
TMP_DATA_TAR="$TMP_UNTAR_FOLDER/$DATA_TAR"
(cd $TMP_UNTAR_FOLDER; ar x $DEB_NAME "$DATA_TAR")
tar xvzf "$TMP_DATA_TAR" -C "$TMP_UNTAR_FOLDER" --wildcards '*libLLVM*.so*' --transform='s/.*\///'
LIB_FILE=`find "$TMP_UNTAR_FOLDER" -maxdepth 1 -name 'libLLVM*.so*' -type f`

if [[ `echo $LIB_FILE | wc -l` -ne 1 ]]; then
  >&2 echo "Error: Not exactly one library available."
  >&2 echo "Remove existing, outdated libraries and invoke this script again."
  >&2 echo "Candidates:"
  >&2 echo $LIB_FILE
  exit 3
fi

CMD="cp ${LIB_FILE} ${EXPECTED_LIB}"
echo $CMD
$CMD
echo "libLLVM-${LLVM_FULL_VERSION}.so extracted successfully."
