package mikufan.cx.conduit.frontend.logic.repo.db

import app.cash.sqldelight.db.SqlDriver
import org.koin.dsl.module

internal actual val dbDriverProvider = module {
  // https://stackoverflow.com/a/78651086/8529009
  provideDbDriver(AppDb.Schema) { driver ->
    single<SqlDriver> { driver }
  }
}