package mikufan.cx.conduit.frontend.logic.repo.db

import app.cash.sqldelight.async.coroutines.awaitMigrate
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import org.w3c.dom.Worker


suspend fun provideDbDriver(
  schema: SqlSchema<QueryResult.AsyncValue<Unit>>,
): SqlDriver {
  val driver = WebWorkerDriver(
    Worker(
      js("""new URL("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url)""")
    )
  )
  schema.awaitMigrate(driver, 0, schema.version)
  return driver
}