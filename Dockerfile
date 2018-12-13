FROM openjdk:8-jdk-alpine

LABEL maintainer="julio.falbo.rj@gmail.com"

ENV SPRING_SERVER_PORT 8087

ADD build/libs/tradeshift-challenge-*.jar tradeshift-challenge.jar

EXPOSE $SPRING_SERVER_PORT

ENTRYPOINT java -jar -Dserver.port=$SPRING_SERVER_PORT tradeshift-challenge.jar
