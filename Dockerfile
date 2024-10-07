# (1) base-image
FROM openjdk:11

# (2) COPY에서 사용될 경로 변수
ARG JAR_FILE=build/libs/*.jar

# (3) jar 빌드 파일을 도커 컨테이너로 복사
COPY ${JAR_FILE} app.jar

# application.yml 파일을 빌드된 경로에서 복사
COPY build/classes/java/main/application.yml application.yml

# (4) jar 파일 실행
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.config.location=classpath:/application.yml"]
