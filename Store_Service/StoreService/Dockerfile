FROM eclipse-temurin:17-jre
COPY target/StoreService-0.0.1-SNAPSHOT.jar /app.jar

WORKDIR /

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app.jar"]