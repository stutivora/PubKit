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
VOLUME ["/data"]

## Sets the working directory within the container
WORKDIR /data

##EXPOSE instructs the container to expose the given port to the host system
EXPOSE 27017

## Run mongo, please note that the mongodb.conf file with db path specified as '/data/db'
## must be placed in /opt/mongodb or whatever directory in the host machine which is mounted to
## to the docker container as /data. Here's a sample docker run command that maps /opt/mongodb dir
## to a docker container as data 'docker run -v /opt/mongodb:/data ubuntu bash'

ENTRYPOINT ["mongod", "-f", "/data/mongodb.conf"]