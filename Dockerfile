FROM navikt/java:13-appdynamics
COPY build/libs/event-handler.jar /app/app.jar
ENV PORT=8090
EXPOSE $PORT
