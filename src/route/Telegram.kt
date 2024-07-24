package dev.schlaubi.telegram.route

import dev.schlaubi.telegram.*
import dev.schlaubi.telegram.models.UserResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.*

fun Route.telegram() {
    authenticate("session-auth") {
        get<Telegram.Initiate> {
            //language=HTML
            call.respondText(
                """
                        <html lang="en">
                        <head>
                          <title>Telegram OAuth Authentication Service</title>
                          <meta name="viewport" content="width=device-width, initial-scale=1.0"> 
                        <body>
                            <style>
                                @import url('https://fonts.googleapis.com/css2?family=Roboto&display=swap');
                                body {
                                    background-color: black;
                                    font-family: 'Roboto', sans-serif;
                                    text-align: center;

                                }
                                
                                .centered-div {
                                    position: fixed;
                                    color: white;
                                    top: 50%;
                                    left: 50%;
                                    transform: translate(-50%, -50%);
                                    background-color: rgb(43, 45, 49);
                                    border-radius: 15px;
                                    padding: 20px;
                                    text-align: center;
                                }
                                @media screen and (max-width: 600px) {
                                    .centered-div {
                                        width: 100% !important;
                                        height: 100% !important;
                                        top: 0 !important;
                                        left: 0 !important;
                                        transform: none !important;
                                        border-radius: 0;
                                    }
                                }
                            </style>

                           
                           <div class='centered-div'>
                                <h2>Please login with Telegram below</h4>
                                <script async src="https://telegram.org/js/telegram-widget.js?22" 
                                data-telegram-login="VgdEvV_bot" data-size="large"
                                data-auth-url="${Config.URL}/telegram/callback"></script>
                            </div>
                        </body>
                        </html>
                        """,
                contentType = ContentType.Text.Html
            )
        }

        get<Telegram.Callback> {
            // https://core.telegram.org/widgets/login
            val dataCheckString = call.parameters
                .filter { key, _ -> key != "hash" }
                .entries()
                // "sorted in alphabetical order"
                .sortedBy(Map.Entry<String, *>::key)
                // "in the format key=<value> with a line feed character"
                .joinToString("\n") { (key, value) -> "$key=${value.first()}" }

            if (hmacSHA256(dataCheckString, Config.TELEGRAM_TOKEN) != it.hash) {
                return@get call.respond(HttpStatusCode.Unauthorized)
            }
            // https://gist.github.com/anonymous/6516521b1fb3b464534fbc30ea3573c2#file-check_authorization-php-L19-L21
            if (System.currentTimeMillis() / 1000 - it.authDate > 86400) {
                return@get call.respond(HttpStatusCode.BadRequest)
            }
            val user = UserResponse(
                it.id.toString(),
                it.username,
                it.firstName,
                it.lastName,
                it.photoUrl
            )

            val request = call.principal<Session>()!!
            val data = sessions.remove(request.id)!!

            val responseUri = URLBuilder(data.redirectUri).apply {
                parameters["code"] = newAuthCode(user)
                parameters["state"] = data.state
            }.build()
            call.respondRedirect(responseUri)
        }
    }
}
