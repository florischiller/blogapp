FROM openjdk:11-jre-slim

RUN mkdir /app

COPY /blogapp.jar /app/blogapp.jar
WORKDIR /app

CMD ["java", "-server", "-jar", "blogapp.jar"]

