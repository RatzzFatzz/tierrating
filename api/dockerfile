FROM openjdk:21-jdk-slim

EXPOSE 8080

RUN groupadd -g 1234 tierrating && \
    useradd -m -u 1234 -g tierrating tierrating

COPY target/tierrating-*.jar /app/tierrating.jar
COPY src/main/resources/application.yml /app
RUN chown -R tierrating:tierrating /app

USER tierrating

WORKDIR /app

ENTRYPOINT [ "java", "-jar", "tierrating.jar"]
