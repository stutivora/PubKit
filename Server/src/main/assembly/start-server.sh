#!/bin/sh

echo "==============================="
echo " ____        _     _  ___ _    "   
echo "|  _ \ _   _| |__ | |/ (_) |_  " 
echo "| |_) | | | | '_ \| ' /| | __| "
echo "|  __/| |_| | |_) | . \| | |_  " 
echo "|_|    \__,_|_.__/|_|\_\_|\__| "
echo "								 "
echo "==============================="

CURRENT_DIR=`pwd`

echo "Starting PubKit Server"
java -jar ${project.artifactId}-${project.version}.jar --spring.config.location=$CURRENT_DIR/config/application.properties $*
echo "PubKit server started...."
