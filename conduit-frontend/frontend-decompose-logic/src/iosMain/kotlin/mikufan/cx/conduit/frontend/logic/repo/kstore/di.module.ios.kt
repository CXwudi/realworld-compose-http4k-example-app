package mikufan.cx.conduit.frontend.logic.repo.kstore

import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val kstoreModule: Module = module {
  single(named(KStoreKey.PERSISTED_CONFIG)) { setupPersistedConfigKStore() }
}