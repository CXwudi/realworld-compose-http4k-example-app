package mikufan.cx.conduit.frontend.logic.repo.db

import app.cash.sqldelight.db.SqlDriver
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * A DI module simply provides an instance of [SqlDriver]
 */
internal expect val dbDriverProvider: Module

// without this it will complain that dbDriverProvider is not initialized
fun getDbDriverProvider() = dbDriverProvider

val dbModule = module {
  includes(getDbDriverProvider())
  singleOf(::getAppDb)

  single { get<AppDb>().userConfigQueries }
}