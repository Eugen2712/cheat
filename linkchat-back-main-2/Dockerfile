FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/linkchat-back-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar"]