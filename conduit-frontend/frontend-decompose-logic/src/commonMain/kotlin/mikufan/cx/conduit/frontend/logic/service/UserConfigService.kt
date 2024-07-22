package mikufan.cx.conduit.frontend.logic.service

import io.github.xxfast.kstore.KStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import mikufan.cx.conduit.frontend.logic.repo.kstore.PersistedConfig


interface UserConfigService {
  val userConfigStateFlow: StateFlow<UserConfigState>

  suspend fun setUrl(url: String? = null)
  suspend fun setToken(token: String? = null)
}

sealed interface UserConfigState {
  data object Loading : UserConfigState
  data class Loaded(
    val url: String? = null,
    val token: String? = null,
  ) : UserConfigState
}

class UserConfigServiceImpl(
  private val kStore: KStore<PersistedConfig>
) : UserConfigService {
  override val userConfigStateFlow: StateFlow<UserConfigState> =
    kStore.updates.map {
      it?.let { UserConfigState.Loaded(it.url, it.token) } ?: UserConfigState.Loading
    }
      .stateIn(
        CoroutineScope(Dispatchers.Default),
        SharingStarted.Lazily,
        UserConfigState.Loading
      )

  override suspend fun setUrl(url: String?) = kStore.update {
    it?.copy(url = url)
  }

  override suspend fun setToken(token: String?) = kStore.update {
    it?.copy(token = token)
  }

}