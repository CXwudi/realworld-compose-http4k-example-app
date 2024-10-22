package mikufan.cx.conduit.frontend.logic.repo.kstore

import io.github.xxfast.kstore.KStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


interface UserConfigKStore {
  /**
   * The default implementation uses a state flow.
   */
  val userConfigFlow: Flow<UserConfigState>

  suspend fun setUrl(url: String? = null)
  suspend fun setToken(token: String? = null)

  suspend fun set(newConfig: PersistedConfig)
  suspend fun reset()
}

sealed interface UserConfigState {
  data object Loading : UserConfigState
  data class Loaded(
    val url: String? = null,
    val token: String? = null,
  ) : UserConfigState
}

class UserConfigKStoreImpl(
  private val kStore: KStore<PersistedConfig>,
) : UserConfigKStore {
  /**
   * Note: under the hood, this is a state flow
   */
  override val userConfigFlow: Flow<UserConfigState> =
    kStore.updates.map {
      it?.let { UserConfigState.Loaded(it.url, it.token) } ?: UserConfigState.Loading
    }

  override suspend fun setUrl(url: String?) = kStore.update {
    if (url == "") throw IllegalArgumentException("url cannot be empty")
    it?.copy(url = url)
  }

  override suspend fun setToken(token: String?) = kStore.update {
    it?.copy(token = token)
  }

  override suspend fun set(newConfig: PersistedConfig) = kStore.set(newConfig)

  override suspend fun reset() = set(PersistedConfig())

}