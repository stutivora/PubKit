##
# Roquito
#
# This creates an java derived base image that installs a recent version of roquito along with dependencies
# such as redis and mongodb
#
##
FROM dockerfile/java:oracle-java7

MAINTAINER narup Puran Singh <puran@32skills.com>

ADD ../lib/roquito-1.0.jar /opt/roquito/
EXPOSE 8080
WORKDIR /opt/roquito/

# Start Roquito App
CMD java -jar lib/${project.artifactId}-${project.version}.jar --spring.config.location=config/application.properties $*