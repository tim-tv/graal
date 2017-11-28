FROM java:8-jre
MAINTAINER Titov Artem github@titovart

ADD build/libs/post-service.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/post-service.jar"]

EXPOSE 8882
