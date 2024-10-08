# JDK 11 이미지 사용
FROM openjdk:11-jdk

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 빌드 결과물인 JAR 파일을 이미지로 복사
COPY build/libs/*.jar app.jar

# jar 파일 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]