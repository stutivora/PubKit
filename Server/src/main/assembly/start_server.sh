#!/bin/sh

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
                    
DIR=$(cd `dirname $0` && pwd)
cd $DIR

echo "Starting PubKit server at"
echo $DIR
echo "....."

java -jar ${project.artifactId}-${project.version}.jar --spring.config.location=$PUBKIT_HOME/config/application.properties $*

echo "PubKit server started...."
