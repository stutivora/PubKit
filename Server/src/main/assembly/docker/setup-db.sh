#! /bin/bash -e

CURRENT_DIR=`pwd`

## Build mongo image
docker build -t pubkit/mongo ./mongo

## Run mongo image
## The -v switch indicates the mapping of host machine's /opt/mongodb directory with the /data directory within the container
## -d flag runs the container in background and print container ID
## Enable this to use external mounted directory
##docker run --name mongodb -d -v $CURRENT_DIR/out/mongodb:/data -p 27017:27017 pubkit/mongo

## Run with default conf and default work dir
docker run --name mongodb -d -p 27017:27017 pubkit/mongo

## Build Redis image
docker build -t pubkit/redis ./redis

## Run redis image, enable this to run redis with external mounted directory
## docker run --name redisdb -d -v $CURRENT_DIR/out/redisdb:/data -p 6379:6379 pubkit/redis

docker run --name redisdb -d -p 6379:6379 pubkit/redis
