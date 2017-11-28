FROM java:8-jre
MAINTAINER Titov Artem github@titovart

ADD build/libs/user-service.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/user-service.jar"]

EXPOSE 8881
