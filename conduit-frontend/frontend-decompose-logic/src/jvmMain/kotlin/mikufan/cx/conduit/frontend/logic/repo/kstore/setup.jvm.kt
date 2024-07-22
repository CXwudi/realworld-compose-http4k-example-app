package mikufan.cx.conduit.frontend.logic.repo.kstore

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import kotlinx.serialization.json.Json
import okio.Path.Companion.toOkioPath
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

fun setupPersistedConfigKStore(): KStore<PersistedConfig> {
  val parentPath = Path("kstore")
  if (!parentPath.exists()) parentPath.createDirectories()
  val path = parentPath.resolve("persisted-config.json")
  return storeOf(
    file = path.toOkioPath(),
    default = PersistedConfig(),
    enableCache = true,
    json = Json {
      prettyPrint = true
    }
  )
}