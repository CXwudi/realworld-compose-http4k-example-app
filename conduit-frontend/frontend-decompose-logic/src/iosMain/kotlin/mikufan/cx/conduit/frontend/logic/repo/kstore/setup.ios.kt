package mikufan.cx.conduit.frontend.logic.repo.kstore

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf

/**
 * Currently not sure if these codes are compilable since I don't have a Mac machine.
 */
fun setupPersistedConfigKStore(): KStore<PersistedConfig> {
  return storeOf(
    file = TODO("Create a folder to save persisted config"),
    default = PersistedConfig(),
  )
}