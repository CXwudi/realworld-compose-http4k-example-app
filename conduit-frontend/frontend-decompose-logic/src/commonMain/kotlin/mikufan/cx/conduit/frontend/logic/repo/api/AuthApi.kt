package mikufan.cx.conduit.frontend.logic.repo.api

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import mikufan.cx.conduit.common.UserLoginDto
import mikufan.cx.conduit.common.UserRegisterDto
import mikufan.cx.conduit.common.UserReq
import mikufan.cx.conduit.common.UserRsp
import mikufan.cx.conduit.frontend.logic.repo.api.util.ConduitResponse

interface AuthApi {
  @POST("users")
  suspend fun register(
    @Body body: UserReq<UserRegisterDto>
  ): ConduitResponse<UserRsp>

  @POST("users/login")
  suspend fun login(
    @Body body: UserReq<UserLoginDto>
  ): ConduitResponse<UserRsp>

}
