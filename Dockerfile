# AS <NAME> to name this stage as maven
FROM maven:latest AS maven

WORKDIR /usr/src/app
COPY . /usr/src/app
# Compile and package the application to an executable JAR
RUN mvn package



# For Java 11,
FROM openjdk:17-alpine

ARG JAR_FILE=SocialNetworkUserBE.jar

WORKDIR /opt/app

EXPOSE 8080

# Copy the spring-boot-api-tutorial.jar from the maven stage to the /opt/app directory of the current stage.
COPY --from=maven /usr/src/app/target/${JAR_FILE} /opt/app/

ENTRYPOINT ["java", "-Dspring.data.mongodb.uri=mongodb://db:27017/userdb","-jar", "SocialNetworkUserBE.jar"]
