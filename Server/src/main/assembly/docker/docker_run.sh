#! /bin/bash

export JAVA_HOME=$(/usr/libexec/java_home -v 1.7)
rm -rf /opt/roquito
mkdir /opt/roquito

docker build -t roquito .

docker run -d -p 8080:8080 roquito