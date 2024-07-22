package mikufan.cx.conduit.frontend.logic.service

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import io.github.xxfast.kstore.KStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import migrations.UserConfig
import mikufan.cx.conduit.frontend.logic.repo.db.UserConfigQueries
import mikufan.cx.conduit.frontend.logic.repo.kstore.PersistedConfig
import org.lighthousegames.logging.logging


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


class UserConfigServiceSqlDelightImpl(
  private val userConfigDao: UserConfigQueries
) : UserConfigService {

  private val userConfigFlow: Flow<UserConfig> = userConfigDao.getSingleConfig().asFlow()
    .mapToOne(Dispatchers.Default)

  override val userConfigStateFlow: StateFlow<UserConfigState> =
    userConfigFlow.map { UserConfigState.Loaded(it.url, it.token) }
      .stateIn(
        CoroutineScope(Dispatchers.Default),
        SharingStarted.Lazily,
        UserConfigState.Loading
      )

  override suspend fun setUrl(url: String?) {
    if (url != null) {
      userConfigDao.setUrl(url)
    } else {
      throw IllegalArgumentException("Invalid URL format, please check and try again")
    }
  }

  override suspend fun setToken(token: String?) {
    userConfigDao.setToken(token)
  }
}

private val log = logging()