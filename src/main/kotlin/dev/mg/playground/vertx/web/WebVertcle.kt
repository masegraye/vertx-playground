package dev.mg.playground.vertx.web

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.http.HttpServer
import io.vertx.core.json.JsonObject
import io.vertx.ext.shell.command.CommandBuilder
import io.vertx.ext.shell.command.CommandRegistry
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.CoroutineVerticle
import java.util.concurrent.atomic.AtomicInteger

class WebVertcle : CoroutineVerticle() {
    private lateinit var server: HttpServer
    private var requestsHandled: Long = 0
    override suspend fun start() {
        server = vertx.createHttpServer()
        server.requestHandler { req ->
            req.response().run {
                requestsHandled += 1
                putHeader("content-type", "text/plain")
                end("Request number: $requestsHandled")
            }
        }
        server.listenAwait(8080)

        vertx.eventBus().consumer<JsonObject>("web.info") { msg ->
            msg.reply(json {
                obj("requestCount" to requestsHandled)
            })
        }
        val cmd = CommandBuilder.command("request-count").let { builder ->
            builder.processHandler { process ->
                vertx.eventBus().request<JsonObject>("web.info", json { obj() }) {
                    if (it.succeeded()) {
                        process.write(it.result().body().encode())
                        process.write("\n")
                    } else {
                        process.write("ERR: ${it.cause().message}")
                    }
                    process.end()
                }
            }
            builder.build(vertx)
        }
        CommandRegistry.getShared(vertx).registerCommand(cmd)
    }
}