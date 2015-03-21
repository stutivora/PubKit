#!/bin/sh

echo "							 "
echo "            _  _____ _____ "  
echo "  _ __ ___ | |/ /_ _|_   _|" 
echo " |\'_\` _ \|\' / | |  | |  "   
echo " | | | | | | . \ | |  | |  "
echo " |_| |_| |_|_|\_\___| |_|  "   
echo "							 "

: ${ROQUITO_HOME:?"ROQUITO_HOME env variable not set"}         
                    
DIR=$(cd `dirname $0` && pwd)
cd $DIR

echo "Starting Roquito server at"
echo $DIR
echo "....."

java -jar ${project.artifactId}-${project.version}.jar --spring.config.location=$ROQUITO_HOME/config/ $*

echo "Roquito Server Started...."
