package mikufan.cx.conduit.frontend.logic.repo.kstore

import kotlinx.serialization.Serializable

@Serializable
data class PersistedConfig(
  val url: String? = null,
  val token: String? = null
)
