package mikufan.cx.conduit.frontend.logic.repo.kstore

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import kotlinx.cinterop.*
import platform.Foundation.*

/**
 * Currently not sure if these codes are compilable since I don't have a Mac machine.
 */
fun setupPersistedConfigKStore(): KStore<PersistedConfig> = storeOf(
  file = (NSFileManager.defaultManager.DocumentDirectory?.relativePath + "kstore/persisted-config.json")
    ?: error("Default document directory is not available"),
  default = PersistedConfig(),
)