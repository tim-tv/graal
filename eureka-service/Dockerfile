FROM java:8-jre
MAINTAINER Titov Artem github@titovart

ADD build/libs/eureka-service-0.1.0.jar /app/eureka-service.jar
CMD ["java", "-Xmx200m", "-jar", "/app/eureka-service.jar"]

EXPOSE 8761
