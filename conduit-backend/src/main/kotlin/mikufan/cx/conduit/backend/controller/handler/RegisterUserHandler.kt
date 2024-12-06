package mikufan.cx.conduit.backend.controller.handler

import mikufan.cx.conduit.backend.controller.userRegistrationLen
import mikufan.cx.conduit.backend.controller.userRspLens
import mikufan.cx.conduit.backend.service.UserService
import mikufan.cx.conduit.common.UserRsp
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

class RegisterUserHandler(
  private val userService: UserService,
) : HttpHandler {

  override fun invoke(request: Request): Response {
    val userDto = userRegistrationLen(request).user
    val userRegisterDto = userService.registerUser(userDto)
    return userRspLens(UserRsp(userRegisterDto), Response(Status.CREATED))
  }
}