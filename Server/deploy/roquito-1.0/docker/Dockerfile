##
# Roquito
#
# This creates an java derived base image that installs a recent version of roquito along with dependencies
# such as redis and mongodb
#
##
FROM dockerfile/java:oracle-java7

MAINTAINER narup Puran Singh <puran@32skills.com>

ADD build/roquito-1.0.jar /opt/roquito/
EXPOSE 8080
WORKDIR /opt/roquito/

# Fix upstart under a virtual host https://github.com/dotcloud/docker/issues/1024
RUN dpkg-divert --local --rename --add /sbin/initctl \
 && ln -s /bin/true /sbin/initctl

# Configure Package Management System (APT) & install MongoDB
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 7F0CEB10 \
 && echo 'deb http://downloads-distro.mongodb.org/repo/ubuntu-upstart dist 10gen' | tee /etc/apt/sources.list.d/mongodb.list \
 && apt-get update \
 && apt-get install -y mongodb-10gen

# Redis server
RUN apt-get install -y redis-server

# Start MongoDB
CMD mongod --fork -f /etc/mongodb.conf \
 && redis-server /etc/redis/redis.conf \
 && bash
 
# Start Roquito App
CMD ["java", "-jar", "roquito-1.0.jar"]