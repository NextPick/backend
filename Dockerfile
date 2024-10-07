# JDK 11 이미지 사용
FROM openjdk:11-jdk

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 빌드 결과물인 JAR 파일을 이미지로 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# application.yml 파일 복사
COPY src/main/resources/application.yml /app/application.yml

# jar 파일 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]