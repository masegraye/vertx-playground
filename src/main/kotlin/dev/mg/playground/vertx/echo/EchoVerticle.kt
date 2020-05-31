package dev.mg.playground.vertx.echo

import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.CoroutineVerticle

class EchoVerticle : CoroutineVerticle() {
    private var operationId: Long? = null

    override suspend fun start() {
        operationId = vertx.setPeriodic(1500L) {
            val payload = json {
                obj("message" to "Hello, world!")
            }
            vertx.eventBus().publish("echo.any", payload)
        }
    }

    override suspend fun stop() {
        if (operationId != null) {
            vertx.cancelTimer(operationId!!)
        }
    }
}