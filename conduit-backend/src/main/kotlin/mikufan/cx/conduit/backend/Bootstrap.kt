package mikufan.cx.conduit.backend

import mikufan.cx.conduit.backend.controller.ConduitServer
import mikufan.cx.inlinelogging.KInlineLogging
import org.flywaydb.core.Flyway

class Bootstrap(
  private val flyway: Flyway,
  private val server: ConduitServer
) : Runnable {
  override fun run() {
    dbMigration()
    startServer()
  }

  private fun dbMigration() {
    log.info { "Performing DB migrations if any" }
    val migrateResult = flyway.migrate()
    if (migrateResult.migrationsExecuted > 0) {
      if (migrateResult.success) {
        log.info { "Database migration successful. Migrations applied: ${migrateResult.migrationsExecuted}" }
      } else {
        val failureString = migrateResult.failedMigrations.joinToString(separator = "\n  ", prefix = "[\n", postfix = "\n") {
          "${it.type} ${it.version} @ ${it.filepath} - ${it.description}"
        }
        val errorMessage = "Database migration failed: \n$failureString"
        log.error { errorMessage }
        throw IllegalStateException(errorMessage)
      }
    } else { // no migration applied
      log.info { "No migration applied" }
    }
  }

  private fun startServer() {
    log.info { "Starting server" }
    server.start()
  }
}

private val log = KInlineLogging.logger()
