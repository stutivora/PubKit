#! /bin/bash

export JAVA_HOME=$(/usr/libexec/java_home -v 1.7)

# Run dependencies
docker run -d -p 8080:8080 roquito