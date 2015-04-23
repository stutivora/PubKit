##
## Dockerfile to create MongoDB image for running PubKit.
##

FROM ubuntu:latest
MAINTAINER Puran Singh <mailtopuran@gmail.com>

RUN \
   apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 7F0CEB10 && \
   echo 'deb http://downloads-distro.mongodb.org/repo/ubuntu-upstart dist 10gen' | sudo tee /etc/apt/sources.list.d/mongodb.list && \
   apt-get update && \
   apt-get install -y mongodb-org

##The VOLUME instruction will tell the container that '/data' is a designated mount point for external hosts or other containers.
##VOLUME ["/data"]

## Sets the working directory within the container

WORKDIR /data

## Create the MongoDB data directory, disable this if 'data' directory is designated as a 
## mount point for external hosts. In that case db directory should be created on host machine.

RUN mkdir -p /data/db

## Bind ip to accept external connections, For production use it's better to restrict bind_ip to 
## specific IPs only
RUN chown -R mongodb:mongodb /data
RUN awk '/bind_ip/{print "bind_ip = 0.0.0.0";next}1' /etc/mongod.conf > /tmp/mongod.conf
RUN cat /tmp/mongod.conf > /etc/mongod.conf 

##EXPOSE instructs the container to expose the given port to the host system
EXPOSE 27017

## Enable this to run mongo with passed in conf file, please note that the mongodb.conf 
## file with db path specified as '/data/db' must be placed in /opt/mongodb or whatever
## directory in the host machine which is mounted to to the docker container as /data. 
## Here's a sample docker run command that maps /opt/mongodb dir to a docker container
## as data 'docker run -v /opt/mongodb:/data ubuntu bash'
## ENTRYPOINT ["mongod", "-f", "/data/mongodb.conf"]

##Run mongo with default configuration.
ENTRYPOINT ["mongod"]
