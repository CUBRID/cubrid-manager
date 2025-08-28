#!/usr/bin/env bash

SHELL_DIR="$( cd "$( dirname "$0" )" && pwd -P )"

VERSION_FILE_PATH=${SHELL_DIR}/VERSION
RELEASE_VERSION_FILE_PATH=${SHELL_DIR}/plugins/com.cubrid.cubridmanager.ui/version.properties
RELEASE_QUERY_VERSION_FILE_PATH=${SHELL_DIR}/plugins/com.cubrid.cubridquery.ui/version.properties

function update_build_version ()
{
  echo "Version File Update....  (com.cubrid.cubridmanager.ui/version.properties)"

  RELEASE_YEAR=$(date "+%Y")
  echo "RELEASE_YEAR=" $RELEASE_YEAR
  sed -i "/releaseStr/d" $RELEASE_VERSION_FILE_PATH
  sed -i "/releaseStr/d" $RELEASE_QUERY_VERSION_FILE_PATH
  echo "releaseStr="$RELEASE_YEAR >> $RELEASE_VERSION_FILE_PATH
  echo "releaseStr="$RELEASE_YEAR >> $RELEASE_QUERY_VERSION_FILE_PATH

  if [ -d ${SHELL_DIR}/.git ]; then
    COMMIT_NUMBER=$(git rev-list --count HEAD | awk '{ printf "%04d", $1 }')
  else  
    COMMIT_NUMBER=0000
  fi

  VERSION=$(cat ${VERSION_FILE_PATH} | grep version | cut -d '=' -f2)
  sed -i "/releaseVersion/d" $RELEASE_VERSION_FILE_PATH
  sed -i "/releaseVersion/d" $RELEASE_QUERY_VERSION_FILE_PATH
  echo "releaseVersion="$VERSION >> $RELEASE_VERSION_FILE_PATH
  echo "releaseVersion="$VERSION >> $RELEASE_QUERY_VERSION_FILE_PATH
  

  RELEASE_VERSION=$VERSION.$COMMIT_NUMBER
  sed -i "/buildVersionId/d" $RELEASE_VERSION_FILE_PATH
  sed -i "/buildVersionId/d" $RELEASE_QUERY_VERSION_FILE_PATH
  echo "buildVersionId="$RELEASE_VERSION >> $RELEASE_VERSION_FILE_PATH
  echo "buildVersionId="$RELEASE_VERSION >> $RELEASE_QUERY_VERSION_FILE_PATH

  echo "VERSION=" $VERSION
  echo "COMMIT_NUMBER=" $COMMIT_NUMBER
  echo "RELEASE_VERSION=" $RELEASE_VERSION
}


if [ ! -z "${JAVA_17_HOME}" ]; then
  echo JAVA_17_HOME: ${JAVA_17_HOME}
  JAVA_HOME=${JAVA_17_HOME}
fi

MVN="`which mvn`"
if [ ! -z "${MAVEN_HOME}" ]; then
  echo MAVEN_HOME: ${MAVEN_HOME}
  MVN="${MAVEN_HOME}/bin/mvn"
fi

if [ -z "$MVN" ]; then
    echo maven not found.
  exit 1
else

    update_build_version
    #$MVN -Dtycho.debug.resolver=true -X -f ./pom.xml clean package
    $MVN -f ./pom.xml clean package -Dcubridtools-version=${RELEASE_VERSION}
fi


