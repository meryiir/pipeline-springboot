FROM openjdk:17-jdk-slim
WORKDIR /app
COPY car/target/car-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]