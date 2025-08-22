#!/bin/sh
WORKSPACE=${HOME}/build/src
JAVA_HOME=${HOME}/build/java
PRODUCT_DIR=cubridmanager
PRODUCT_NAME=CUBRIDManager
ECLIPSE_HOME=${HOME}/build/eclipse_for_build/eclipse
BUILD_HOME=${WORKSPACE}
BUILD_DIR=${BUILD_HOME}/${PRODUCT_DIR}/com.cubrid.cubridmanager.build
VERSION_DIR=${BUILD_HOME}/${PRODUCT_DIR}/com.cubrid.cubridmanager.ui
VERSION_FILE_PATH=${VERSION_DIR}/version.properties

echo "Version File Update.... (${VERSION_FILE_PATH})"

if [ -d ${BUILD_HOME}/${PRODUCT_DIR}/.git ]; then
  COMMIT_NUMBER=$(cd ${BUILD_HOME}/${PRODUCT_DIR} && git rev-list --count HEAD | awk '{ printf "%04d", $1 }' 2> /dev/null)
  [ $? -ne 0 ] && COMMIT_NUMBER=$(cd ${BUILD_HOME}/${PRODUCT_DIR} && git log --oneline | wc -l)
else
  COMMIT_NUMBER=0000
fi

RELEASE_VERSION=$(cat ${VERSION_FILE_PATH} | grep releaseVersion | cut -d '=' -f2)

echo "RELEASE_VERSION = " $RELEASE_VERSION
echo "COMMIT_NUMBER = " $COMMIT_NUMBER
FULL_VERSION=buildVersionId=${RELEASE_VERSION}.${COMMIT_NUMBER}

sed -i '/buildVersionId/d' ${VERSION_FILE_PATH}
echo $FULL_VERSION >> ${VERSION_FILE_PATH}

VERSION=`grep buildVersionId ${VERSION_FILE_PATH} | awk 'BEGIN {FS="="} ; {print $2}'`
CUR_VER_DIR=`date +%Y%m%d`
CUR_VER_DIR=cubridmanager-deploy/${CUR_VER_DIR}_${VERSION}
OUTPUT_DIR=${BUILD_HOME}/${CUR_VER_DIR}
MAKENSIS_EXEC_PATH=${HOME}/build/nsis/makensis.exe
MAKENSIS_INPUT_PATH="c:/build/src/${PRODUCT_DIR}/com.cubrid.cubridmanager.build/deploy"
MAKENSIS_OUTPUT_PATH="c:/build/src/${CUR_VER_DIR}"

echo "${PRODUCT_NAME} ${VERSION} build is started..."
rm -rf ${OUTPUT_DIR}
mkdir -p ${OUTPUT_DIR}
cd ${BUILD_HOME}
${JAVA_HOME}/bin/java -jar ${ECLIPSE_HOME}/plugins/org.eclipse.equinox.launcher_1.1.0.v20100507.jar -application org.eclipse.ant.core.antRunner -buildfile ${BUILD_DIR}/build.xml -Doutput.path=${OUTPUT_DIR} -Declipse.home=${ECLIPSE_HOME} -Dmakensis.path=${MAKENSIS_EXEC_PATH} -Dmakensis.input.path=${MAKENSIS_INPUT_PATH} -Dmakensis.output.path=${MAKENSIS_OUTPUT_PATH} -Dproduct.version=${VERSION} dist
