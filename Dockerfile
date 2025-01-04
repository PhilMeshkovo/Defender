FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY target/Defender-0.0.1-SNAPSHOT.jar defender.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "defender.jar"]
