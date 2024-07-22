package mikufan.cx.conduit.frontend.logic.repo.kstore

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import okio.Path.Companion.toPath

fun setupPersistedConfigKStore(): KStore<PersistedConfig> = storeOf(
  file = "kstore/persisted-config.json".toPath(),
  default = PersistedConfig(),
  enableCache = true,
)