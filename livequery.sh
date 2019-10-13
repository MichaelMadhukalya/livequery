#!/bin/bash

ROOT=$1
NATIVE=$1/native
LIB=$1/lib
TARGET=$1/target

LIBPOLL_SO=libpoll.so
LOG_NAME=application.log

#Ensure that root level app dir is passed to the script
if [ ! "$#" -gt "0" ]; then
    echo "Launch script at least needs one arg. Please specify app root path"
    exit 1
fi

clear
cd $ROOT
echo "Starting livequery at root:" $ROOT
echo ""
sleep 1

# Echo JAVA_HOME dir along with gcc version for compiling native code
echo "Java home located at: " $JAVA_HOME
echo "Displaying gcc version details"
gcc --version
echo ""
sleep 1

cd $LIB
rm -rf $LIBPOLL_SO
echo "Deleting previous installation of native lib (.so) file:" $LIBPOLL_SO
echo ""
sleep 1

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
sleep 1

# Publish Maven version details
mvn --version
echo ""
sleep 1

mvn clean
mvn compile
mvn package

# Launch livequery application from target path
cd $TARGET
echo "Launching livequery using working dir: $TARGET"
java -jar -Xms512M -Xmx2048M -Dfile.encoding=UTF-8 -Djava.library.path=$LIB -Dlivequery.root=$ROOT -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 livequery-1.0-SNAPSHOT.jar >/dev/null 2>&1

# Exit
echo "Exiting livequery application. Please check log: " $TARGET/$LOG_NAME
echo "Bye!"
echo ""
exit 0


