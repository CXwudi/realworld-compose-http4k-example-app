package mikufan.cx.conduit.frontend.logic.repo.kstore

import android.app.Application
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import okio.Path.Companion.toOkioPath

fun setupPersistedConfigKStore(application: Application): KStore<PersistedConfig> {
  val parentPath = application.filesDir.resolve("kstore")
  if (!parentPath.exists()) parentPath.mkdirs()
  val path = parentPath.resolve("persisted-config.json")
  return storeOf(
    file = path.toOkioPath(),
    default = PersistedConfig(),
    enableCache = true,
  )
}