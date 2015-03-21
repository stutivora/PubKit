#!/bin/sh

echo "							 "
echo "            _  _____ _____ "  
echo "  _ __ ___ | |/ /_ _|_   _|" 
echo " |\'_\` _ \|\' / | |  | |  "   
echo " | | | | | | . \ | |  | |  "
echo " |_| |_| |_|_|\_\___| |_|  "   
echo "							 "

if [ "$ROQUITO_HOME" = "" ]
then
   echo "ROQUITO_HOME not set. Please set the ROQUITO_HOME value to your roquito directory."
else
   echo "ROQUITO_HOME:".$ROQUITO_HOME
fi         
                    
DIR=$(cd `dirname $0` && pwd)
cd $DIR

echo "Starting Roquito server at"
echo $DIR
echo "....."

java -jar ${project.artifactId}-${project.version}.jar $*

echo "Roquito Server Started...."
