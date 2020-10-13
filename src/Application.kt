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

fun main(args: Array<String>) {
    embeddedServer(
        factory = Netty,
        port = 8080,
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
            call.respond(Response(status = "OK"))
        }

        post("/") {
            val request = call.receive<Request>()
            call.respond(HttpStatusCode.OK, request)
        }
    }
}

data class Response(val status: String)

data class Request(
    val id: String,
    val quantity: Int,
    val isTrue: Boolean
)