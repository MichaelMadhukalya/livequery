#!/bin/bash

ROOT=$1
NATIVE=$1/native
LIB=$1/lib
TARGET=$1/target

LIBPOLL_SO=libpoll.so
LOG_NAME=application.log

clear
cd $ROOT
echo "Starting livequery at root:" $ROOT
echo ""

# Echo JAVA_HOME dir along with gcc version for compiling native code
echo "Java home located at: " $JAVA_HOME
echo "Displaying gcc version details"
gcc --version
echo ""

cd $LIB
rm -rf $LIBPOLL_SO
echo "Deleteing previous installation of native lib (.so) file:" $LIBPOLL_SO
echo ""

cd $NATIVE
gcc -fPIC -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" -shared -o $LIB/$LIBPOLL_SO com_livequery_agent_filesystem_core_FileChangeProcessor.c

# Check if native shared executable (.so) was created successfully in the previous step
cd $LIB
if [ ! -f $LIB/$LIBPOLL_SO ]; then
    echo "Native shared executable (.so) $LIBPOLL_SO not found insde $LIB dir. Exit!"
    echo ""
    exit 1
fi

# Clean, compile and package livequery application (Maven)
cd $ROOT
echo "Compiling livequery application at root: $ROOT. This will cleanup previous targets!"
echo ""

mvn clean
mvn compile
mvn package

# Launch livequery application from target path
cd $TARGET
echo "Launching livequery JVM inside working dir: $TARGET"
echo ""
java -jar -Xms512M -Xmx2048M -Dfile.encoding=UTF-8 -Djava.library.path=$LIB -Dlivequery.root=$ROOT livequery-1.0-SNAPSHOT.jar >/dev/null 2>&1
echo "Exiting livequery application. Bye!"
echo ""
exit 0


