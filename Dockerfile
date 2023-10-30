# jdk 17 Image Start (기본 이미지 지정)
FROM openjdk:17-jdk
# 인자 정리 - Jar (jar파일 위치 지정)
ARG JAR_FILE=build/libs/*.jar
# copy jar file : COPY[생성된 로컬 jar파일 경로][컨테이너 내부 jar 파일 경로]
COPY ${JAR_FILE} cicd-0.0.1-SNAPSHOT.jar
EXPOSE 8080
# 컨테이너 실행 시 자동으로 jar파일 실행
ENTRYPOINT ["java", "-Dspring,profiles.active=docker", "-jar", "cicd-0.0.1-SNAPSHOT.jar"]
