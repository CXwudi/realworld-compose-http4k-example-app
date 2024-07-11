package mikufan.cx.conduit.frontend.logic.repo.db

import app.cash.sqldelight.async.coroutines.awaitMigrate
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver

suspend fun provideDbDriver(
  schema: SqlSchema<QueryResult.AsyncValue<Unit>>
): SqlDriver {
  val driver = NativeSqliteDriver(schema.synchronous(), "db.db")
  schema.awaitMigrate(driver, 0, schema.version)
  return driver
}