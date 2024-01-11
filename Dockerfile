FROM gradle:jdk21 as builder
WORKDIR /usr/app
COPY . .
RUN ./gradlew --no-daemon installDist

FROM eclipse-temurin:21-jre-alpine

WORKDIR /usr/app
COPY --from=builder /usr/app/build/install/telegram-oauth .

ENTRYPOINT ["/usr/app/bin/telegram-oauth"]