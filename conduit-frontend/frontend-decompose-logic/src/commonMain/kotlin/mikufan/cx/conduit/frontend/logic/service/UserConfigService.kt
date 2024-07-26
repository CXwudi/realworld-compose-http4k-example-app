package mikufan.cx.conduit.frontend.logic.service

import io.github.xxfast.kstore.KStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mikufan.cx.conduit.frontend.logic.repo.kstore.PersistedConfig


interface UserConfigService {
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

class UserConfigServiceImpl(
  private val kStore: KStore<PersistedConfig>,
  private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
) : UserConfigService {
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