package mikufan.cx.conduit.frontend.logic.repo.db

import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.SqlDriver

fun getAppDb(sqlDriver: SqlDriver): AppDb {
  val appDb = AppDb(sqlDriver)

  // for some reason, sqldelight doesn't able to pick up migration, but schema generation via migration file is working
//  sqlDriver.execute(0, "INSERT OR IGNORE INTO user_config VALUES (1, NULL, NULL, NULL)", 0)
  AppDb.Schema.migrate(
    driver = sqlDriver,
    oldVersion = 0,
    newVersion = AppDb.Schema.version,
    AfterVersion(2) { println("migrated to version 1") },
  )
  return appDb
}