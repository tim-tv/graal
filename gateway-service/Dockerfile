FROM java:8-jre
MAINTAINER Titov Artem github@titovart

ADD build/libs/gateway-service-0.1.0.jar /app/gateway-service.jar
CMD ["java", "-Xmx200m", "-jar", "/app/gateway-service.jar"]

EXPOSE 8080
