package mikufan.cx.conduit.backend

import io.github.oshai.kotlinlogging.KotlinLogging
import mikufan.cx.conduit.backend.controller.ConduitServer

class Bootstrap(
  private val server: ConduitServer
) : Runnable {

  override fun run() {
    startServer()
  }

  private fun startServer() {
    log.info { "Starting server" }
    server.start()
  }
}

private val log = KotlinLogging.logger {}
