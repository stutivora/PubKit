#
# Redis Dockerfile for running PubKit. Based on https://github.com/dockerfile/redis
#

# Pull base image.
FROM ubuntu:latest

# Install Redis.
RUN apt-get update && apt-get install -y redis-server

# Define mountable directories.
VOLUME ["/data"]

# Define working directory.
WORKDIR /data

# Define default command.
#ENTRYPOINT  ["/usr/bin/redis-server", "/etc/redis/redis.conf"]
ENTRYPOINT  ["/usr/bin/redis-server"]

# Expose ports.
EXPOSE 6379