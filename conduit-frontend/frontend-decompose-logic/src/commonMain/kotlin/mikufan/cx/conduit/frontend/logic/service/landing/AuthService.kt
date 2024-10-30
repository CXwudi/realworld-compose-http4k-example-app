package mikufan.cx.conduit.frontend.logic.service.landing

import mikufan.cx.conduit.common.UserDtoUtils
import mikufan.cx.conduit.frontend.logic.repo.api.AuthApi
import mikufan.cx.conduit.frontend.logic.repo.api.util.getOrThrow
import mikufan.cx.conduit.frontend.logic.repo.kstore.UserConfigKStore

interface AuthService {
  suspend fun login(email: String, password: String)
  suspend fun register(email: String, username: String, password: String)
  suspend fun reset()
}

class DefaultAuthService(
  private val authApi: AuthApi,
  private val userConfigKStore: UserConfigKStore,
) : AuthService {

  override suspend fun login(email: String, password: String) {
    val req = UserDtoUtils.createLoginReq(email, password)
    val rsp = authApi.login(req)
    val userRsp = rsp.getOrThrow()
    val token = userRsp.user.token
    if (token.isNullOrBlank()) error("Token is null or blank")
    userConfigKStore.setToken(token)
  }

  override suspend fun register(email: String, username: String, password: String) {
    val req = UserDtoUtils.createRegisterReq(email, username, password)
    val rsp = authApi.register(req)
    val userRsp = rsp.getOrThrow()
    val token = userRsp.user.token
    if (token.isNullOrBlank()) error("Token is null or blank")
    userConfigKStore.setToken(token)
  }

  override suspend fun reset() {
    userConfigKStore.reset()
  }


}