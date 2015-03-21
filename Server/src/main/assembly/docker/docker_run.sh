#! /bin/bash

echo "							 "
echo "            _  _____ _____ "  
echo "  _ __ ___ | |/ /_ _|_   _|" 
echo " |\'_\` _ \|\' / | |  | |  "   
echo " | | | | | | . \ | |  | |  "
echo " |_| |_| |_|_|\_\___| |_|  "   
echo "							 "

: ${ROQUITO_HOME:?"ROQUITO_HOME env variable not set"}

export JAVA_HOME=$(/usr/libexec/java_home -v 1.7)

DIR=$(cd `dirname $0` && pwd)
cd $DIR

echo "Starting docker build for image Roquito at"
echo $DIR
docker build -t roquito .

echo "Running docker build for image Roquito"
docker run -d -p 8080:8080 roquito
echo "Running Roquito"