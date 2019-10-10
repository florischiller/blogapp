FROM openjdk:13-jdk-oracle

ARG JAR_NAME

RUN mkdir /app
RUN yum install -y iproute iptables

COPY "${JAR_NAME}.jar" "/app/${JAR_NAME}.jar"
WORKDIR /app

CMD ["java", "-server", "-jar", "${JAR_NAME}.jar"]

