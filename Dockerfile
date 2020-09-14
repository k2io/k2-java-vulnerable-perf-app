FROM adoptopenjdk/openjdk8:centos-jre

MAINTAINER harshit@k2io.com

COPY target/k2-java-vulnerable-perf-1.0.0.jar /k2-java-vulnerable-perf-1.0.0.jar

CMD ["/bin/bash", "-c", "java ${K2_OPTS} -jar /k2-java-vulnerable-perf-1.0.0.jar"]



