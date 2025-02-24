package mikufan.cx.conduit.backend

import com.zaxxer.hikari.HikariDataSource
import io.github.oshai.kotlinlogging.KotlinLogging
import mikufan.cx.conduit.backend.controller.ConduitServer

/**
 * The main entry point for starting and stopping the Conduit server.
 *
 * This class is responsible for starting the server and registering a shutdown hook
 * to ensure the data source is closed gracefully when the application exits.
 *
 * @param server The ConduitServer instance to start and stop.
 * @param dataSource The HikariDataSource instance to close on shutdown.
 */
class Bootstrap(
  private val server: ConduitServer,
  private val dataSource: HikariDataSource
) : Runnable {

  override fun run() {
    registerShutdownHook()
    startServer()
  }

  private fun registerShutdownHook() {
    Runtime.getRuntime().addShutdownHook(Thread {
      shutdownDataSource()
    })
  }

  private fun shutdownDataSource() {
    log.info { "Shutting down data source" }
    dataSource.close()
  }

  private fun startServer() {
    log.info { "Starting server" }
    server.start()
  }
}

private val log = KotlinLogging.logger {}
