package mikufan.cx.conduit.frontend.logic.service

import mikufan.cx.conduit.frontend.logic.repo.kstore.KStoreKey
import mikufan.cx.conduit.frontend.logic.repo.repoModule
import org.koin.core.qualifier.named
import org.koin.dsl.module


/**
 * Require [repoModule]
 */
val serviceModule = module {
  single<UserConfigService> { UserConfigServiceImpl(get(named(KStoreKey.PERSISTED_CONFIG))) }
}