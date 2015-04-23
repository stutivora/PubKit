#! /bin/bash

echo "==============================="
echo " ____        _     _  ___ _    "   
echo "|  _ \ _   _| |__ | |/ (_) |_  " 
echo "| |_) | | | | '_ \| ' /| | __| "
echo "|  __/| |_| | |_) | . \| | |_  " 
echo "|_|    \__,_|_.__/|_|\_\_|\__| "
echo "								 "
echo "==============================="

export JAVA_HOME=$(/usr/libexec/java_home -v 1.7)

DIR=$(cd `dirname $0` && pwd)
cd $DIR

echo "Starting docker build for image PubKit at"
echo $DIR

## Buid PubKit docker image
docker build -t pubkit/server .

echo "Running docker build for image PubKit"

docker run --name pubkit -d -p 80:80 pubkit/server

echo "Started PubKit Server..."
