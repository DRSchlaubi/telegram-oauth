FROM eclipse-temurin:22-jre-alpine

WORKDIR /usr/app
COPY build/install/telegram-oauth .

ENTRYPOINT ["/usr/app/bin/telegram-oauth"]
