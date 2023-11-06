# AS <NAME> to name this stage as maven
FROM maven:latest AS maven

WORKDIR /usr/src/app
COPY . /usr/src/app
# Compile and package the application to an executable JAR
RUN mvn package

# For Java 11,
FROM openjdk:17-alpine

ARG JAR_FILE=SocialNetworkUserBE.jar

ARG PORT=8081

EXPOSE $PORT

WORKDIR /opt/app

# Copy the spring-boot-api-tutorial.jar from the maven stage to the /opt/app directory of the current stage.
COPY --from=maven /usr/src/app/target/${JAR_FILE} /opt/app/

ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-Dspring.data.mongodb.uri=mongodb://vm.cloud.cbh.kth.se:2690/userdb","-jar", "SocialNetworkUserBE.jar"]
