FROM java:8-jre
MAINTAINER Titov Artem github@titovart

ADD build/libs/auth-service-0.1.0.jar /app/auth-service.jar
CMD ["java", "-Xmx200m", "-jar", "/app/auth-service.jar"]

EXPOSE 8885
