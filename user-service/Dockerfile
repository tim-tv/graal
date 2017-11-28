FROM java:8-jre
MAINTAINER Titov Artem github@titovart

ADD build/libs/user-service-0.1.0.jar /app/user-service.jar
CMD ["java", "-Xmx200m", "-jar", "/app/user-service.jar"]

EXPOSE 8881
