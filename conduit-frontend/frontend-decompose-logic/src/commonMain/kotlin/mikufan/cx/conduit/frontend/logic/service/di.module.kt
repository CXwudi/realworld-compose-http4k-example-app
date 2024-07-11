package mikufan.cx.conduit.frontend.logic.service

import mikufan.cx.conduit.frontend.logic.repo.db.dbModule
import org.koin.dsl.module


/**
 * Require [dbModule]
 */
val serviceModule = module {
  single<UserConfigService> { UserConfigServiceImpl(get()) }
}