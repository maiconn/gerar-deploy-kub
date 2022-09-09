FROM openjdk:18-jdk-slim
COPY /target/*.jar /usr/local/lib/app.jar
EXPOSE 80
ENV JAVA_OPTS="{{javaOpts}}"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /usr/local/lib/app.jar" ]