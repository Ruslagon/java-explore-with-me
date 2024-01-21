FROM eclipse-temurin:11-jre-jammy
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY target/*.jar app.jar
# RUN apk update && \
#     apk add --no-cache tzdata
# run apk add -U tzdata
# ENV TZ=Europe/Moscow
# RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENTRYPOINT ["java","-jar","/app.jar"]
#COPY ${JAR_FILE} app.jar
#ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]