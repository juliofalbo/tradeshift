FROM openjdk:8-jdk-alpine

ENV SPRING_SERVER_PORT 8087

ADD build/libs/tradeshift-challenge-*.jar /app.jar

EXPOSE $SPRING_SERVER_PORT

ENTRYPOINT java -jar -Dserver.port=$SPRING_SERVER_PORT app.jar
