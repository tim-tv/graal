FROM java:8-jre
MAINTAINER Titov Artem github@titovart

ADD build/libs/statistic-service-0.1.0.jar /app/statistic-service.jar
CMD ["java", "-Xmx200m", "-jar", "/app/statistic-service.jar"]

EXPOSE 8886
