#! /bin/bash

echo "==============================="
echo " ____        _     _  ___ _    "   
echo "|  _ \ _   _| |__ | |/ (_) |_  " 
echo "| |_) | | | | '_ \| ' /| | __| "
echo "|  __/| |_| | |_) | . \| | |_  " 
echo "|_|    \__,_|_.__/|_|\_\_|\__| "
echo "								 "
echo "==============================="

if [ "$PUBKIT_HOME" = "" ]
then
   echo "PUBKIT_HOME not set. Please set the PUBKIT_HOME value to your PubKit directory."
else
   echo "PUBKIT_HOME:".$PUBKIT_HOME
fi
export JAVA_HOME=$(/usr/libexec/java_home -v 1.7)

DIR=$(cd `dirname $0` && pwd)
cd $DIR

echo "Starting docker build for image PubKit at"
echo $DIR
docker build -t PubKit .

echo "Running docker build for image PubKit"
docker run -d -p 8080:8080
echo "Running PubKit"