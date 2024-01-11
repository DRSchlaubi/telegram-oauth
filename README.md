# telegram-oauth

Telegram OAuth server for [Telegram Login for Websites](https://core.telegram.org/widgets/login)

This small tool allows you to deploy use Telegrams non-oauth compliant login API with OAuth 2.0 and OIDC compliant
tools like [authentik](https://goauthentik.io/)

# Limitations

These will likely remain, as this is mostly a project for my own use, feel free to copy the code and make your own
if you need more features

- Only a single OAuth client is supported
- Only the `grant_type: authorization_code` and `response_type: code` are supported
- Only the OIDC authorize, token and profile endpoints are supported

# Setup

The application is distributed under the
[ghcr.io/drschlaubi/telegram-oauth](https://github.com/DRSchlaubi/telegram-oauth/pkgs/container/telegram-oauth/165793339?tag=main)
docker image and can be setup the following way

1. Set up a Telegram bot
    1. Message [@BotFather](https://t.me/BotFather) on Telegram
    2. Type `/newbot` and enter a bot Display name and username
    3. use the `/setdomain` command to set the Domain used for this application
2. Setup the application
    1. [Install Docker](https://docs.docker.com/engine/install/)
    2. Download the [docker-compose file](docker-compose.yml)
    3. Create a `.env` file like this and run `docker compose up -d`
      
```
TELEGRAM_TOKEN=<telegram token>
URL=<url from step 1.3>
JWT_SECRET=<can be generated using pwgen>
OAUTH_CLIENT_ID=authentik
OAUTH_CLIENT_SECRET=<can be generated using pwgen>
OAUTH_REDIRECT_URIS=<redirect uri>
```

# OAuth endpoints

These are directly compatible with Authentik, just put the URLs in.

Authorize: `<URL>/oauth/authorize`
Token: `<URL>/oauth/token`
Profile: `<URL>/oauth/profile`

## Profile example 
```json5
{
  "sub": "telegram user id",
  "name": "telegram user name",
  "given_name": "telegram first name",
  "family_name": "telegram last name",
  "picture": "<url to t.me>"
}
```