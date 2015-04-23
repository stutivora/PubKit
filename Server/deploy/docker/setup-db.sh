#! /bin/bash -e

## Build mongo image
docker build -t pubkit/mongo ./mongo

## Run mongo image
## The -v switch indicates the mapping of host machine's /opt/mongodb directory with the /data directory within the container
## -d flag runs the container in background and print container ID
docker run --name mongodb -d -v /opt/mongodb:/data -p 27017:27017 pubkit/mongo

## Build Redis image
docker build -t pubkit/redis ./redis

## Run redis image
docker run --name redisdb -d -v /opt/redisdb:/data -p 6379:6379 pubkit/redis