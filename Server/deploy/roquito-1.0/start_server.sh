#!/bin/sh

echo "							 "
echo "            _  _____ _____ "  
echo "  _ __ ___ | |/ /_ _|_   _|" 
echo " |\'_\` _ \|\' / | |  | |  "   
echo " | | | | | | . \ | |  | |  "
echo " |_| |_| |_|_|\_\___| |_|  "   
echo "							 "                             
DIR=`dirname $0`
cd $DIR
java -jar lib/roquito-1.0.jar --spring.config.location=config/application.properties $*

echo "mKit Server Started...."