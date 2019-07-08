FROM navikt/java:12
COPY build/libs/event-handler.jar /app/app.jar
EXPOSE 8090