package mikufan.cx.conduit.frontend.logic.service

import mikufan.cx.conduit.frontend.logic.repo.db.dbModule
import mikufan.cx.conduit.frontend.logic.repo.kstore.KStoreKey
import org.koin.core.qualifier.named
import org.koin.dsl.module


/**
 * Require [dbModule]
 */
val serviceModule = module {
//  single<UserConfigService> { UserConfigServiceSqlDelightImpl(get()) }
  single<UserConfigService> { UserConfigServiceImpl(get(named(KStoreKey.PERSISTED_CONFIG))) }
}