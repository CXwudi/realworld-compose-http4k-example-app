package mikufan.cx.conduit.frontend.logic.service

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.stateIn
import migrations.UserConfig
import mikufan.cx.conduit.frontend.logic.repo.db.UserConfigQueries


interface UserConfigService {
  val userConfigStateFlow: StateFlow<UserConfigState>

  suspend fun setUrl(url: String? = null)
  suspend fun setUsername(username: String? = null)
  suspend fun setToken(token: String? = null)
}

sealed interface UserConfigState {
  data object Loading : UserConfigState
  data class Loaded(
    val url: String? = null,
    val username: String? = null,
    val token: String? = null,
  ) : UserConfigState
}

class UserConfigServiceImpl(
  private val userConfigDao: UserConfigQueries
) : UserConfigService {

  private val userConfigFlow: Flow<UserConfig> = userConfigDao.getSingleConfig().asFlow()
    .mapToOne(Dispatchers.Default)

  override val userConfigStateFlow: StateFlow<UserConfigState> =
    userConfigFlow.map { UserConfigState.Loaded(it.url, it.username, it.token) }
      .stateIn(
        CoroutineScope(Dispatchers.Default),
        SharingStarted.Lazily,
        UserConfigState.Loading
      )

  override suspend fun setUrl(url: String?) {
    if (url != null) {
      setSingleConfig(url = url)
    } else {
      throw IllegalArgumentException("Invalid URL format, please check and try again")
    }
  }

  override suspend fun setUsername(username: String?) {
    setSingleConfig(username = username)
  }

  override suspend fun setToken(token: String?) {
    setSingleConfig(token = token)
  }

  internal suspend fun setSingleConfig(
    url: String? = null,
    username: String? = null,
    token: String? = null,
  ) {
    val single = userConfigFlow.single()
    val newUrl = url ?: single.url
    val newUsername = username ?: single.username
    val newToken = token ?: single.token
    userConfigDao.setSingleConfig(url = newUrl, username = newUsername, token = newToken)
  }
}