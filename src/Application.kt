package br.com.kitchen.stock

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.event.Level

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080

    embeddedServer(
        factory = Netty,
        port = port,
        module = Application::module
    ).apply { start(wait = true) }
}

fun Application.module() {
    install(AutoHeadResponse)

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    install(StatusPages) {
        exception { throwable: Throwable ->
            call.respondText(throwable.localizedMessage, ContentType.Text.Plain, HttpStatusCode.InternalServerError)
        }
    }

    routing {
        get("/") {
            call.respond(mapOf("Status" to "OK"))
        }
    }
}
