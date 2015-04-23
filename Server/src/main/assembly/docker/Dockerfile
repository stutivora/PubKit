##
# PubKit
#
# This creates an java derived base image that installs a recent version of PubKit along with dependencies
# such as redis and mongodb
#
##
FROM dockerfile/java:oracle-java7
MAINTAINER narup Puran Singh <mailtopuran@gmail.com>

WORKDIR /data

COPY ${project.artifactId}-${project.version}.jar /data/
COPY config /data/

## Expose port 80, if using SSL change this to 443
EXPOSE 80

# Start PubKit App
CMD java -jar ${project.artifactId}-${project.version}.jar $*