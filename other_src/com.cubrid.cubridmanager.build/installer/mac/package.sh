#!/bin/sh

PACKAGE_NAME=""
PACKAGE_VERSION=""
DS_STORE_TAR_PATH=""
INPUT_PATH=""
OUTPUT_PATH=""

while :; do
	case $1 in
		--name=?*)
			PACKAGE_NAME=${1#*=}
			;;
		--version=?*)
			PACKAGE_VERSION=${1#*=}
			;;
		--dsstore=?*)
			DS_STORE_TAR_PATH=${1#*=}
			;;
		--input=?*)
			INPUT_PATH=${1#*=}
			;;
		--output=?*)
			OUTPUT_PATH=${1#*=}
			;;
		*)
			break
	esac
	shift
done

if [ -z ${PACKAGE_NAME} ] || [ -z ${PACKAGE_VERSION} ] || [ -z ${DS_STORE_TAR_PATH} ] || [ -z ${INPUT_PATH} ] || [ -z ${OUTPUT_PATH} ];
then
	echo "ERROR Invalid arguments"
	exit
fi

if [ ! -d ${INPUT_PATH} ];
then
	echo "ERROR ${INPUT_PATH} is not a directory"
	exit
fi
tar xvf ${DS_STORE_TAR_PATH} -C ${INPUT_PATH}

if [ ! -d ${OUTPUT_PATH} ];
then
	echo "ERROR ${OUTPUT_PATH} is not a directory"
	exit
fi
genisoimage -V ${PACKAGE_NAME} -D -R -apple -no-pad -o ${OUTPUT_PATH}/${PACKAGE_NAME}-${PACKAGE_VERSION}-macosx-cocoa.dmg ${INPUT_PATH}

if [ ! -e ${OUTPUT_PATH}/${PACKAGE_NAME}-${PACKAGE_VERSION}-macosx-cocoa.dmg ];
then
        echo "ERROR Failed to build dmg file"
fi
