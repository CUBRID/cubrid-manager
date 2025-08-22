#!/bin/bash
#
# make product
#

echo start time: `date`

REPOSITORY_DIR=`pwd`
PRODUCT_NAME="CUBRIDManager"
INSTALL_NAME="cubridmanager"

VERSION="2013"
IN_PATH=${REPOSITORY_DIR}/cubridmanager
OUT_PATH=${REPOSITORY_DIR}
MACHINE=`uname -m`
if [ ${MACHINE} = "x86_64" ]; then
    PLATFORM="linux-x86_64"
else
    PLATFORM="linux-i386"
fi

# Using getopts
if [ $# -eq 4 ] || [ $# -eq 8 ];
then
    while getopts v:o:i:p: options
    do
       case $options in
    	   v) VERSION=$OPTARG;;
	       i) IN_PATH=$OPTARG;;
	       o) OUT_PATH=$OPTARG;;
	       p) PLATFORM=$OPTARG;;
	       ?)
            echo "Usage: `basename $0` -v version -i input -o output -p linux.i386"
       esac
    done
fi

# make version script
echo "version=2013" >> version.sh
echo "BuildNumber=${VERSION}" >> version.sh

PRODUCT_FILE=${PRODUCT_NAME}-${VERSION}-${PLATFORM}.sh
GZ_FILE=${PRODUCT_NAME}-${VERSION}-${PLATFORM}.tar.gz

# make shell archive
# remake GZ_FILE for shell archive
mkdir -p ${INSTALL_NAME}
cp -rf ${IN_PATH}/* ${INSTALL_NAME}
tar cf ${PRODUCT_NAME}-product.tar ${INSTALL_NAME}
gzip -f ${PRODUCT_NAME}-product.tar

cp -f ${PRODUCT_NAME}_install.sh ${OUT_PATH}/${PRODUCT_FILE}
tar cf - ${PRODUCT_NAME}-product.tar.gz ${PRODUCT_NAME}_Setup.sh COPYING version.sh >> ${OUT_PATH}/${PRODUCT_FILE}

rm -rf ${INSTALL_NAME}
rm version.sh ${PRODUCT_NAME}-product.tar.gz

echo end time: `date`
echo SUCCESS 
