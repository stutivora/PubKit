##
# PubKit
#
# This creates an java derived base image that installs a recent version of PubKit along with dependencies
# such as redis and mongodb
#
##
FROM dockerfile/java:oracle-java7

MAINTAINER narup Puran Singh <puran@32skills.com>

ADD ${project.artifactId}-${project.version}.jar /data/${project.artifactId}-${project.version}.jar

EXPOSE 8080

# Start PubKit App
CMD java -jar ${project.artifactId}-${project.version}.jar --spring.config.location=$PUBKIT_HOME/config/application.properties $*