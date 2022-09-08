#
# Build stage
#
FROM maven:3.8.6-openjdk-18-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM openjdk:18-jdk-slim
COPY --from=build /home/app/target/*.jar /usr/local/lib/app.jar
EXPOSE 80
ENV JAVA_OPTS="{{javaOpts}}"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /usr/local/lib/app.jar" ]
