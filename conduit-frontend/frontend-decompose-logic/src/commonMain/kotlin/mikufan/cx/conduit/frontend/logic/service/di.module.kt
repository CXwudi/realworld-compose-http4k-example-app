package mikufan.cx.conduit.frontend.logic.service

import mikufan.cx.conduit.frontend.logic.repo.kstore.KStoreKey
import mikufan.cx.conduit.frontend.logic.repo.kstore.kstoreModule
import org.koin.core.qualifier.named
import org.koin.dsl.module


/**
 * Require [kstoreModule]
 */
val serviceModule = module {
  single<UserConfigService> { UserConfigServiceImpl(get(named(KStoreKey.PERSISTED_CONFIG))) }
}