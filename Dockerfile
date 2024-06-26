FROM openjdk:11
ARG JAR_FILE=build/libs/*.jar
COPY ./target/authentication-0.0.1-SNAPSHOT.jar TxT-auth.jar
ENTRYPOINT ["java", "-jar", "/TxT-auth.jar"]
