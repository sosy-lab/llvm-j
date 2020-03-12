#!/bin/bash

function download_and_extract {

    TMP_PACKAGE=Packages
    if [[ -f "$TMP_PACKAGE" ]]; then
        >&2 echo "$TMP_PACKAGE already exists. Moved to $TMP_PACKAGE.old"
        mv "$TMP_PACKAGE" "$TMP_PACKAGE.old"
    fi
    wget http://apt.llvm.org/$UBUNTU_VERSION/dists/llvm-toolchain-$UBUNTU_VERSION-${LLVM_VERSION}/main/binary-amd64/Packages.gz -O "$TMP_PACKAGE".gz
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

    DATA_TAR=$(ar t "$DEB_NAME"| grep data.tar)
    TMP_DATA_TAR="$TMP_LLVM_FOLDER/$DATA_TAR"
    (cd $TMP_LLVM_FOLDER; ar x $DEB_NAME "$DATA_TAR")
    tar xf "$TMP_DATA_TAR" -C "$TMP_LLVM_FOLDER" --wildcards '*libLLVM*.so*' --transform='s/.*\///'

    if [[ `echo $LIB_FILE | wc -l` -ne 1 ]]; then
      >&2 echo "Error: Not exactly one library available."
      >&2 echo "Candidates:"
      >&2 echo $LIB_FILE
      exit 3
    fi

    ## Get dependencies of libLLVM
    extract_library "http://mirrors.edge.kernel.org/ubuntu/pool/main/n/ncurses/libtinfo5_6.1-1ubuntu1_amd64.deb" "libtinfo"
    extract_library "http://mirrors.kernel.org/ubuntu/pool/main/libe/libedit/libedit2_3.1-20170329-1_amd64.deb" "libedit"
}

function extract_library() {
    URL=$1
    NAME=$2

    TMP_FOLDER="$TMP_DEPS_FOLDER/$NAME"
    mkdir -p "$TMP_FOLDER"
    DEB=$TMP_FOLDER/libedit2.deb
    wget "$URL" -O "$DEB"
    (cd "$TMP_FOLDER"; ar x "$DEB" data.tar.xz)
    # extract all shared libraries of libedit because of symlinks
    tar xf "$TMP_FOLDER/data.tar.xz" -C "$TMP_FOLDER" --wildcards '*'"$NAME"'*.so*' --transform='s/.*\///'
}

# Download the LLVM shared library of the version number
# given on the command line.
# We use packages prepared for Ubuntu 12.04 to ensure compatibility
# with older systems.
UBUNTU_VERSION=trusty

set -e

TMP=/tmp/llvm-j-$RANDOM
if [[ ! -d "$TMP" ]]; then
    mkdir -p "$TMP"
fi

TMP_LLVM_FOLDER="$TMP"
TMP_DEPS_FOLDER=$TMP/deps
TMP_TINFO_FOLDER=$TMP_DEPS_FOLDER/libtinfo
TMP_EDIT_FOLDER=$TMP_DEPS_FOLDER/libedit
TMP_LIBBSD_FOLDER=$TMP_DEPS_FOLDER/libedit

LLVM_FULL_VERSION=$1
if [[ -z $LLVM_FULL_VERSION ]]; then
  >&2 echo "Usage: ./download_lib.sh LLVM_VERSION"
  exit 10
fi

# Cut minor revisions
LLVM_VERSION=`echo $LLVM_FULL_VERSION | cut -d'.' -f1,2`

if ! hash chrpath 2> /dev/null; then
    >&2 echo "Application chrpath required but not installed."
    exit 6
fi

(cd $TMP && download_and_extract "$@")

LIB_FILE=`find "$TMP_LLVM_FOLDER" -maxdepth 1 -name 'libLLVM*.so*' -type f`

# change rpath of libLLVM to take our libtinfo and libedit without
# the need to set LD_LIBRARY_PATH whenever libLLVM should be used
chrpath -r '$ORIGIN' "$LIB_FILE"

EXPECTED_LIB="libLLVM-${LLVM_FULL_VERSION}.so"
if [[ -e $EXPECTED_LIB ]]; then
  >&2 echo "$EXPECTED_LIB already exists. That shouldn't be possible."
  exit 5
fi

EXPECTED_FOLDER_DEPS="./"

CMD="cp -L $TMP_TINFO_FOLDER/libtinfo.so.5 $TMP_EDIT_FOLDER/libedit.so.2 $EXPECTED_FOLDER_DEPS"
echo $CMD
$CMD

CMD="cp ${LIB_FILE} ${EXPECTED_LIB}"
echo $CMD
$CMD

echo "libLLVM-${LLVM_FULL_VERSION}.so extracted successfully."
