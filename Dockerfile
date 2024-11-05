FROM --platform=$TARGETOS/$TARGETARCH eclipse-temurin:23-jre-alpine

WORKDIR /usr/app
COPY build/install/telegram-oauth .

ENTRYPOINT ["/usr/app/bin/telegram-oauth"]
