# 멀티 스테이지 빌드
FROM openjdk:17-jdk-slim AS builder

WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# Gradle Wrapper 실행 권한 부여
RUN chmod +x ./gradlew

# 애플리케이션 빌드
RUN ./gradlew bootJar

# 런타임 스테이지
FROM openjdk:17-jdk-slim

WORKDIR /app

## 시간대 설정 (선택사항)
#RUN apt-get update && \
#    DEBIAN_FRONTEND=noninteractive apt-get install -y tzdata && \
#    ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
#    echo "Asia/Seoul" > /etc/timezone


# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app/app.jar"]