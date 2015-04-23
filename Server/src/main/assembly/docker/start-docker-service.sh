#! /bin/bash

echo "==============================="
echo " ____        _     _  ___ _    "   
echo "|  _ \ _   _| |__ | |/ (_) |_  " 
echo "| |_) | | | | '_ \| ' /| | __| "
echo "|  __/| |_| | |_) | . \| | |_  " 
echo "|_|    \__,_|_.__/|_|\_\_|\__| "
echo "								 "
echo "==============================="

echo "Starting docker build for image PubKit"

cp ../${project.artifactId}-${project.version}.jar .
cp -R ../config .

## Buid PubKit docker image
docker build -t pubkit/server .

echo "Running docker build for image PubKit"

docker run --name pubkit -d -p 8080:8080 pubkit/server

echo "Started PubKit Server..."