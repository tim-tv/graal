FROM java:8-jre
MAINTAINER Titov Artem github@titovart

ADD build/libs/hashtag-service-0.1.0.jar /app/hashtag-service.jar
CMD ["java", "-Xmx200m", "-jar", "/app/hashtag-service.jar"]

EXPOSE 8883
