package mikufan.cx.conduit.frontend.logic.repo.api

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import mikufan.cx.conduit.common.NewUserReq
import mikufan.cx.conduit.common.UserRsp

interface AuthApi {
  @POST("users")
  suspend fun register(
    @Body body: NewUserReq
  ): UserRsp
}
