package mikufan.cx.conduit.backend.db

import mikufan.cx.conduit.backend.config.Config
import mikufan.cx.conduit.backend.config.configModule
import mikufan.cx.conduit.backend.db.repo.UserRepo
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * require [configModule]
 */
val dbModule = module {
  single { creatDataSource(get<Config>().db) }
  singleOf(::createExposedDb)
  singleOf(::createConduitTransactionManager)

  singleOf(::UserRepo)
}