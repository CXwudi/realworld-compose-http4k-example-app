package mikufan.cx.conduit.frontend.logic.repo.db

import app.cash.sqldelight.async.coroutines.awaitMigrate
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

suspend fun provideDbDriver(
  schema: SqlSchema<QueryResult.AsyncValue<Unit>>
): SqlDriver {
  val driver = JdbcSqliteDriver("jdbc:sqlite:db/db.sqlite")
  schema.awaitMigrate(driver, 0, schema.version)
  return driver
}