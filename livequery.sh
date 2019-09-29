#!/bin/bash

ROOT=$1
NATIVE=$1/native
LIB=$1/lib
TARGET=$1/target
LOG_NAME=application.log

NATIVE_SHARED_OBJECT=libpoll.so

clear
cd $ROOT
echo "Starting livequery at root:" $ROOT

# Echo common paths and where they are found
echo "Java home located at: " $JAVA_HOME

cd $LIB
rm -rf $NATIVE_LIB
echo "Deleteing previous installation of native lib:" $NATIVE_LIB

cd $NATIVE
gcc -fPIC -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" -shared -o $LIB/NATIVE_SHARED_OBJECT com_livequery_agent_filesystem_core_FileChangeProcessor.c

# Check if native shared executable (.so) was created successfully
cd $LIB
if [ ! -f "$NATIVE_SHARED_OBJECT" ]
then
    echo "File '${NATIVE_SHARED_OBJECT}' not found. Exit!!"
    exit(1)
fi

# Clean, compile and package livequery application
cd $ROOT
echo "Compiling livequery application at root: '${ROOT}'. This will cleanup previous targets!"
mvn clean
mvn compile
mvn package

# Launch livequery application
cd $TARGET
echo "Launching livequery JVM at path : '${TARGET}'"
java -jar -Djava.library.path=$LIB -Dlivequery.root=$root livequery-1.0-SNAPSHOT.jar >> /dev/null 2>&1
echo "Exiting livequery application. Bye!!"
exit(0)


