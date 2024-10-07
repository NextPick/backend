# (1) base-image
FROM openjdk:11

# (2) COPY에서 사용될 경로 변수
ARG JAR_FILE=build/libs/*.jar

# (3) jar 빌드 파일을 도커 컨테이너로 복사
COPY ${JAR_FILE} app.jar

COPY src/main/resources/application.yml /application.yml

# (4) jar 파일 실행
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-Dspring.config.location=classpath:/application.yml,/secret/application-secret.yml", "-jar", "/myapp.jar"]