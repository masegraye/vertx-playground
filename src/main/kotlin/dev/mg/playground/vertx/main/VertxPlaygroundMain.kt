package dev.mg.playground.vertx.main

import dev.mg.playground.vertx.shell.ShellVerticle
import io.vertx.core.Vertx

fun main() {
    val vertx = Vertx.vertx()

    vertx.exceptionHandler { ex ->
        System.err.println("Error: ${ex.message}; ${ex.cause}")
    }

    vertx.deployVerticle(ShellVerticle())
}
