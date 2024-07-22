package mikufan.cx.conduit.frontend.logic.repo.kstore

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.storage.storeOf

fun setupPersistedConfigKStore(): KStore<PersistedConfig> = storeOf("persisted_config", default = PersistedConfig())