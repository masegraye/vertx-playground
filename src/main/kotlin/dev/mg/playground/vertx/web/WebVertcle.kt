package dev.mg.playground.vertx.web

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.MessageConsumer
import io.vertx.core.http.HttpServer
import io.vertx.core.json.JsonObject
import io.vertx.ext.shell.command.Command
import io.vertx.ext.shell.command.CommandBuilder
import io.vertx.ext.shell.command.CommandRegistry
import io.vertx.kotlin.core.eventbus.unregisterAwait
import io.vertx.kotlin.core.http.closeAwait
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.ext.shell.command.registerCommandAwait
import io.vertx.kotlin.ext.shell.command.unregisterCommandAwait
import java.util.concurrent.atomic.AtomicInteger

class WebVertcle : CoroutineVerticle() {
    private lateinit var server: HttpServer
    private lateinit var consumer: MessageConsumer<JsonObject>
    private var requestsHandled = AtomicInteger(0)
    override suspend fun start() {
        server = vertx.createHttpServer()
        server.requestHandler { req ->
            req.response().run {

                putHeader("content-type", "text/plain")
                end("Request number: ${requestsHandled.incrementAndGet()}")
            }
        }
        server.listenAwait(8080)

        consumer = vertx.eventBus().consumer("web.info")
        consumer.handler { msg ->
            msg.reply(json {
                obj("requestCount" to requestsHandled)
            })
        }

        CommandBuilder
                .command("request-count")
                .let { builder ->
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
                .let {
                    CommandRegistry.getShared(vertx).registerCommandAwait(it)
                }
    }
}