# 빌드 스테이지
FROM openjdk:11-jdk AS build

# 작업 디렉토리 설정
WORKDIR /workspace/app

# Gradle 파일들을 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 소스 코드 복사
COPY src src

# Gradle 빌드 실행 (테스트 제외)
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

# 실행 스테이지
FROM openjdk:11-jdk

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일을 복사
COPY --from=build /workspace/app/build/libs/*.jar app.jar

# application.yml 파일 복사
COPY --from=build /workspace/app/src/main/resources/application.yml /app/application.yml

# 컨테이너 실행 시 실행할 명령
ENTRYPOINT ["java", "-jar", "/app/app.jar"]