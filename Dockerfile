FROM openjdk:21-jdk-slim
RUN java -version
WORKDIR /app
COPY /target/techshop-message-producer-0.0.1-SNAPSHOT.jar /app/techshop-message-producer.jar
CMD ["java", "-jar", "/app/techshop-message-producer.jar"]
