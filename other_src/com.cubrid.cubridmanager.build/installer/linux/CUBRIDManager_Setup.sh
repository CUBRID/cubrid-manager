#!/bin/sh -f


PRODUCT_NAME=CUBRIDManager
INSTALL_NAME=cubridmanager
target_input=""

JAVA_VERSION=`java -version 2>&1 | awk '/java version/ {print $3}'|sed 's/"//g'|awk '{if ($1>=1.6) print "ok"}'`
if [ "$JAVA_VERSION" != "ok" ]; then
    echo "Please install Java 1.6 or later"
    exit 1
fi

PACKAGE_TMP_DIR=".package_tmp_dir"

atexit() {
    rm -rf ${PACKAGE_TMP_DIR} >/dev/null 2>&1
    while [ $? != 0 ];
    do
        rm -rf ${PACKAGE_TMP_DIR} >/dev/null 2>&1
    done
    exit $1;
}
trap 'atexit 1' HUP INT TERM

gzip -d ${PRODUCT_NAME}-product.tar.gz

mkdir $PACKAGE_TMP_DIR > /dev/null 2>&1
tar --extract --no-same-owner --file=${PRODUCT_NAME}-product.tar -C $PACKAGE_TMP_DIR > /dev/null 2>&1
if [ $? != 0 ]; then
    tar xfo ${PRODUCT_NAME}-product.tar -C $PACKAGE_TMP_DIR > /dev/null 2>&1
    if [ $? != 0 ]; then
        atexit 1
    fi
fi

java_binpath=`which java`
jre_bitinfo=`file -L $java_binpath  | awk '{print $3}'`
prod_bitinfo=`file -L $PACKAGE_TMP_DIR/${INSTALL_NAME}/cubridmanager | awk '{print $3}'`
MACHINE=`uname -m`

if [ ${MACHINE} = "x86_64" ]; then
    os_bitinfo="64-bit"
else
    os_bitinfo="32-bit"
fi

if [ ${prod_bitinfo} != ${os_bitinfo} ]; then
    echo "Your OS is ${os_bitinfo}, but ${PRODUCT_NAME} is ${prod_bitinfo}, please install the correct ${PRODUCT_NAME}."
    atexit 1
fi

if [ ${prod_bitinfo} != $jre_bitinfo ]; then
    echo "Your ${PRODUCT_NAME} is ${prod_bitinfo}, but JRE is $jre_bitinfo, please install the correct JRE."
    atexit 1
fi

if [ ! -z ${CUBRID} ]; then
    echo $CUBRID
    DEFAULT_INSTALL_DIR=${CUBRID}/$INSTALL_NAME
else
    DEFAULT_INSTALL_DIR=${HOME}/CUBRID/$INSTALL_NAME
fi

cat COPYING | more
echo -n "Do you agree to the above license terms? (yes or no) : "
read agree_terms

if [ "$agree_terms" != "yes" ]; then
	echo "If you don't agree to the license you can't install this software."
	atexit 0
fi

echo -n "Do you want to install this software($PRODUCT_NAME) to the default(${DEFAULT_INSTALL_DIR}) directory? (yes or no) [Default: yes] : "
read ans_install_dir

if [ "$ans_install_dir" = "no" ] || [ "$ans_install_dir" = "n" ]; then
	echo -n "Input the $PRODUCT_NAME install directory. [Default: ${DEFAULT_INSTALL_DIR}] : "
	read target_input
fi

if [ "$target_input" = "" ]; then
    target_dir=$DEFAULT_INSTALL_DIR
else
    target_dir=$target_input
fi

echo "Install ${PRODUCT_NAME} to '$target_dir' ..."

if [ -d $target_dir ]; then
    echo "Directory '$target_dir' exist! "
    echo "If a ${PRODUCT_NAME} service is running on this directory, it may be terminated abnormally."
    echo "And if you don't have right access permission on this directory(subdirectories or files), install operation will be failed."
    echo -n "Overwrite anyway? (yes or no) [Default: no] : "
    read overwrite

    if [ "$overwrite" != "y" ] && [ "$overwrite" != "yes" ]; then
        atexit 0
    fi
fi

mkdir -p $target_dir  > /dev/null 2>&1

if [ -d $target_dir ]; then
    rm -rf $target_dir > /dev/null 2>&1
fi

mv -f ${PACKAGE_TMP_DIR}/${INSTALL_NAME} ${target_dir} > /dev/null 2>&1

if [ $? = 0 ]; then
	  chmod u+x ${target_dir}/${INSTALL_NAME} > /dev/null 2>&1
    echo ""
    echo "${PRODUCT_NAME} has been successfully installed."
    echo ""
    atexit 0
else
    echo ""
    echo "Cannot install ${PRODUCT_NAME}."
    echo ""
    atexit 1
fi

