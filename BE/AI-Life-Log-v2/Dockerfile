# Dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

# 빌드된 실행 가능한 jar 파일 복사 (plain.jar가 아님)
COPY build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]