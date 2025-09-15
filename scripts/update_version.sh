#!/usr/bin/env bash

SHELL_DIR="$( cd "$( dirname "$0" )" && pwd -P )"
PROJECT_DIR=$(dirname ${SHELL_DIR})
VERSION_FILE_PATH=${PROJECT_DIR}/VERSION
RELEASE_VERSION_FILE_PATH=${SHELL_DIR}/plugins/com.cubrid.cubridmanager.ui/version.properties

echo "PROJECT_DIR=" $PROJECT_DIR
cd ${PROJECT_DIR}

VERSION=$(cat ${VERSION_FILE_PATH} | grep version | cut -d '=' -f2)
echo "VERSION=" $VERSION

echo "Version Update For pom.xml."
POM_FILE_LIST=$(find ${PROJECT_DIR} -name pom.xml)
for POM_FILE in ${POM_FILE_LIST}
do
  echo "POM_FILE=" $POM_FILE
  sed -i '0,/<version>/s#<version>.*</version>#<version>'${VERSION}'-SNAPSHOT</version>#' "$POM_FILE"
done

echo "Version Update For feature.xml."
FEATURE_FILE_LIST=$(find ${PROJECT_DIR} -name feature.xml)
for FEATURE_FILE in ${FEATURE_FILE_LIST}
do
  echo "FEATURE_FILE=" $FEATURE_FILE
  sed -i '/<feature/,/>/s/version="[^"]*"/version="'${VERSION}'.qualifier"/' ${FEATURE_FILE}
done


echo "Version Update For MANIFEST.MF."
MANIFEST_FILE_LIST=$(find ${PROJECT_DIR} -name MANIFEST.MF)
for MANIFEST_FILE in ${MANIFEST_FILE_LIST}
do
  echo "MANIFEST_FILE=" $MANIFEST_FILE
  sed -i "s/Bundle-Version: .*/Bundle-Version: ${VERSION}.qualifier/" ${MANIFEST_FILE}
done

echo "Version Update For com.cubrid.cubridmanager.app.product."
PRODUCT_FILE_LIST=$(find ${PROJECT_DIR} -name com.cubrid.cubridmanager.app.product)
for PRODUCT_FILE in ${PRODUCT_FILE_LIST}
do
  echo "PRODUCT_FILE=" $PRODUCT_FILE
  sed -i "/<product/,/>/s/version=\".*\"/version=\"${VERSION}.qualifier\"/" ${PRODUCT_FILE}
  sed -i "/<feature/,/>/s/version=\".*\"/version=\"${VERSION}.qualifier\"/" ${PRODUCT_FILE}
done

echo "Version Update For site.xml."
SITE_FILE_LIST=$(find ${PROJECT_DIR}/site -name site.xml)
for SITE_FILE in ${SITE_FILE_LIST}
do
  echo "SITE_FILE=" $SITE_FILE
  sed -i 's/_[0-9.]\+\.qualifier\.jar"/_'${VERSION}'.qualifier.jar"/g' ${SITE_FILE}
  sed -i 's/version="[0-9.]\+\.qualifier"/version="'${VERSION}'.qualifier"/g' ${SITE_FILE}
done

echo "Version Update For category.xml."
CATEGORY_FILE_LIST=$(find ${PROJECT_DIR}/site -name category.xml)
for CATEGORY_FILE in ${CATEGORY_FILE_LIST}
do
  echo "CATEGORY_FILE=" $CATEGORY_FILE
  sed -i 's/_[0-9.]\+-SNAPSHOT\.jar"/_'${VERSION}'-SNAPSHOT.jar"/g' ${CATEGORY_FILE}
  sed -i '/<feature/s/version="[0-9.]\+"/version="'${VERSION}'"/g' ${CATEGORY_FILE}
done