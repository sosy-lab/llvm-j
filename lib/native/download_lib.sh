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

wget -N http://apt.llvm.org/precise/dists/llvm-toolchain-precise-${LLVM_VERSION}/main/binary-amd64/Packages.gz
gunzip Packages.gz
CANDIDATE_LINES=`grep "Filename:" Packages | grep libllvm${LLVM_VERSION}_`

if [[ `echo $CANDIDATE_LINES | wc -l` -ne 1 ]]; then
  >&2 echo "Error: Not exactly one possible candidate. Candidates:"
  >&2 echo $CANDIDATE_LINES
  exit 1
fi

DEB_SUFFIX=`echo $CANDIDATE_LINES | cut -d":" -f2 | tr -d " "`
DEB_NAME=`echo $DEB_SUFFIX | rev | cut -d"/" -f1 | rev`

# Download deb if it doesn't exist yet
if [[ ! -e $DEB_NAME ]]; then
    DEB_URL="http://apt.llvm.org/precise/${DEB_SUFFIX}"
    if wget $DEB_URL; then
      echo "Download successful"
    else
      >&2 echo "Error: wget failed (see above)."
      exit 2
    fi
fi

ar x $DEB_NAME data.tar.gz
tar xvzf data.tar.gz --wildcards '*libLLVM*.so*' --transform='s/.*\///'
LIB_FILE=`find . -maxdepth 1 -name 'libLLVM*.so*' -type f`

if [[ `echo $LIB_FILE | wc -l` -ne 1 ]]; then
  >&2 echo "Error: Not exactly one library available."
  >&2 echo "Remove existing, outdated libraries and invoke this script again."
  >&2 echo "Candidates:"
  >&2 echo $LIB_FILE
  exit 3
fi

CMD="mv ${LIB_FILE} ${EXPECTED_LIB}"
echo $CMD
$CMD
echo "libLLVM-${LLVM_FULL_VERSION}.so extracted successfully."
