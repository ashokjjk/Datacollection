FROM openjdk:8
ADD target/datacoll.jar datacoll.jar
ENTRYPOINT ["java", "-jar", "datacoll.jar"]