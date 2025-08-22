#!/bin/sh 

PRODUCT_NAME=CUBRIDManager

remove_files() {
    rm -f ./${PRODUCT_NAME}_Setup.sh ${PRODUCT_NAME}-product.tar.gz ${PRODUCT_NAME}-product.tar ./install.${PRODUCT_NAME}.tmp ./version.sh COPYING
}
atexit() {
    remove_files
    while [ $? != 0 ];
    do
        remove_files
    done
    exit $1;
}
trap 'atexit 1' HUP INT TERM

install_file="$0"
tail +30 "$install_file" > install.${PRODUCT_NAME}.tmp 2> /dev/null
if [ "$?" != "0" ]
then
  tail -n +30 "$install_file" > install.${PRODUCT_NAME}.tmp 2> /dev/null
fi

tar xf install.${PRODUCT_NAME}.tmp 2> /dev/null

./${PRODUCT_NAME}_Setup.sh "$0"

atexit 0
