package mikufan.cx.conduit.frontend.logic.repo.api

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import io.ktor.client.statement.HttpResponse
import mikufan.cx.conduit.common.UserLoginDto
import mikufan.cx.conduit.common.UserRegisterDto
import mikufan.cx.conduit.common.UserReq
import mikufan.cx.conduit.common.UserRsp

interface AuthApi {
  @POST("users")
  suspend fun register(
    @Body body: UserReq<UserRegisterDto>
  ): UserRsp

  @POST("users/login")
  suspend fun login(
    @Body body: UserReq<UserLoginDto>
  ): HttpResponse

}
