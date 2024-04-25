FROM openjdk:17-jdk-slim

ADD ./bin/opentp-server-jar-with-dependencies.jar /app.jar

ENTRYPOINT ["java","-jar","app.jar"]
