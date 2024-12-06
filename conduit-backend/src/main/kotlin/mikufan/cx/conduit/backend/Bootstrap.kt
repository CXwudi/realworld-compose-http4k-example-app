package mikufan.cx.conduit.backend

import mikufan.cx.conduit.backend.controller.ConduitServer
import mikufan.cx.inlinelogging.KInlineLogging

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

private val log = KInlineLogging.logger()
