FROM openjdk:13-jdk-oracle
RUN mkdir /app
RUN yum install -y iproute iptables

COPY "blogapp.jar" "/app/blogapp.jar"
WORKDIR /app

CMD ["java", "-server", "-jar", "blogapp.jar"]

