#-------------------------------------------------------------
# 1단계: 커스텀 JRE 생성 (jlink)
#-------------------------------------------------------------
FROM eclipse-temurin:21-alpine as builder-jre

RUN apk add --no-cache binutils

RUN $JAVA_HOME/bin/jlink \
         --module-path "$JAVA_HOME/jmods" \
         --verbose \
         --add-modules ALL-MODULE-PATH \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /jre

#-------------------------------------------------------------
# 2단계: Gradle 빌드
#-------------------------------------------------------------
FROM gradle:8.5-jdk21-alpine AS build
WORKDIR /app

# 캐시 최적화: 의존성 먼저 다운로드
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN --mount=type=cache,target=/home/gradle/.gradle ./gradlew dependencies || true

# 전체 복사 후 빌드
COPY . .
RUN chmod +x ./gradlew
RUN --mount=type=cache,target=/home/gradle/.gradle ./gradlew bootJar

#-------------------------------------------------------------
# 3단계: 최종 실행 이미지 (Alpine + custom JRE)
#-------------------------------------------------------------
FROM alpine:3.18
ENV JAVA_HOME=/jre
ENV PATH="$JAVA_HOME/bin:$PATH"

# 루트에서 tzdata 먼저 설치
RUN apk add --no-cache tzdata && \
    ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone

# 사용자 생성 및 권한 설정
ARG APPLICATION_USER=appuser
RUN adduser --no-create-home -u 1000 -D $APPLICATION_USER && \
    mkdir /app && chown -R $APPLICATION_USER /app

# JRE 및 앱 복사
COPY --from=builder-jre /jre $JAVA_HOME
COPY --chown=1000:1000 --from=build /app/build/libs/*.jar /app/app.jar

# 환경변수 설정
ENV TZ=Asia/Seoul

# 권한 전환
USER 1000
WORKDIR /app

# 실행
ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-Duser.timezone=Asia/Seoul", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
EXPOSE 8080