FROM gradle:8.5-jdk21 AS builder

WORKDIR /app

# 의존성 캐시를 활용하려면 build.gradle 먼저 복사하고 의존성만 먼저 설치
COPY build.gradle settings.gradle ./
COPY gradlew .
COPY gradle ./gradle
RUN chmod +x gradlew
RUN ./gradlew dependencies

# 소스 코드는 나중에 복사 (변경될 일이 많기 때문)
COPY src ./src
RUN ./gradlew bootjar

# 실제 런타임 이미지
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
