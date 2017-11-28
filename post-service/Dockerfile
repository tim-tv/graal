FROM java:8-jre
MAINTAINER Titov Artem github@titovart

ADD build/libs/post-service-0.1.0.jar /app/post-service.jar
CMD ["java", "-Xmx200m", "-jar", "/app/post-service.jar"]

EXPOSE 8882
