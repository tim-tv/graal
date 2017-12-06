FROM java:8-jre
MAINTAINER Titov Artem github@titovart

ADD build/libs/aggregation-service-0.1.0.jar /app/aggregation-service.jar
CMD ["java", "-Xmx200m", "-jar", "/app/aggregation-service.jar"]

EXPOSE 8884
