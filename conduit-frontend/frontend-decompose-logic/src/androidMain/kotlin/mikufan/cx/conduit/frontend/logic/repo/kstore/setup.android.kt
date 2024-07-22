package mikufan.cx.conduit.frontend.logic.repo.kstore

import android.app.Application
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import okio.Path.Companion.toOkioPath

fun setupPersistedConfigKStore(application: Application): KStore<PersistedConfig> = storeOf(
  file = application.filesDir.resolve("kstore/persisted-config.json").toOkioPath(),
  default = PersistedConfig(),
  enableCache = true,
)