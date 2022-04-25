FROM openjdk:17
ADD build/libs/schedule-service-0.0.1-SNAPSHOT.jar schedule-service-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "schedule-service-0.0.1-SNAPSHOT.jar"]
