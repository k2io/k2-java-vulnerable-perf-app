FROM nimmis/java-centos:openjdk-8-jre
MAINTAINER harshit@k2io.com

COPY k2-java-vulnerable-perf-1.0.0.jar /k2-java-vulnerable-perf-1.0.0.jar

CMD ["/bin/bash", "-c", "java -jar /k2-java-vulnerable-perf-1.0.0.jar"]



