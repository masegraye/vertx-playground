package dev.mg.playground.vertx.shell

import io.vertx.ext.shell.ShellService
import io.vertx.ext.shell.ShellServiceOptions
import io.vertx.ext.shell.term.TelnetTermOptions
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.ext.shell.startAwait
import io.vertx.kotlin.ext.shell.stopAwait

class ShellVerticle : CoroutineVerticle() {
    private var service: ShellService? = null

    override suspend fun start() {
        service = ShellService
            .create(vertx, ShellServiceOptions().apply {
                telnetOptions = TelnetTermOptions().apply {
                    host = "localhost"
                    port = 4000
                }
            })
        service?.startAwait()
    }

    override suspend fun stop() {
        service?.stopAwait()
    }
}