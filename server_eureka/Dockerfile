FROM openjdk:17-jdk-slim
WORKDIR /app
COPY server_eureka/target/server_eureka-*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]