##
# PubKit docker image
##
FROM dockerfile/java:oracle-java7
MAINTAINER narup Puran Singh <mailtopuran@gmail.com>

WORKDIR /data

ADD ${project.artifactId}-${project.version}.jar /data/${project.artifactId}-${project.version}.jar
ADD config /data

## Expose port 8080
EXPOSE 8080

# Start PubKit
CMD java -jar ${project.artifactId}-${project.version}.jar --spring.config.location=./config/application.properties $*