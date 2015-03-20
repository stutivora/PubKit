##
# Roquito
#
# This creates an java derived base image that installs a recent version of roquito along with dependencies
# such as redis and mongodb
#
##
FROM dockerfile/java:oracle-java7

MAINTAINER narup Puran Singh <puran@32skills.com>

ADD roquito-1.0.jar /data/roquito-1.0.jar

EXPOSE 8080

# Start Roquito App
CMD java -jar roquito-1.0.jar --spring.config.location=application.properties $*