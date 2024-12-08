package mikufan.cx.conduit.frontend.logic.service.main

import mikufan.cx.conduit.frontend.logic.component.main.me.LoadedMe
import mikufan.cx.conduit.frontend.logic.repo.api.AuthApi
import mikufan.cx.conduit.frontend.logic.repo.api.util.getOrThrow
import mikufan.cx.conduit.frontend.logic.repo.kstore.UserConfigKStore

interface MePageService {
  suspend fun getCurrentUser(): LoadedMe
  suspend fun logout()
  suspend fun switchServer()
}

class DefaultMePageService(
  private val authApi: AuthApi,
  private val userConfigKStore: UserConfigKStore,
) : MePageService {

  override suspend fun getCurrentUser(): LoadedMe {
    val rsp = authApi.getCurrentUser()
    val userRsp = rsp.getOrThrow()

    return LoadedMe(
      username = userRsp.user.username,
      bio = userRsp.user.bio ?: "",
      imageUrl = userRsp.user.image ?: "",
    )
  }

  override suspend fun logout() {
    userConfigKStore.setToken(null)
  }

  override suspend fun switchServer() {
    userConfigKStore.reset()
  }
}
