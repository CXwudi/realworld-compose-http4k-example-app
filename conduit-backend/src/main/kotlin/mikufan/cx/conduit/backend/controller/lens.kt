package mikufan.cx.conduit.backend.controller

import mikufan.cx.conduit.common.ErrorRsp
import mikufan.cx.conduit.common.UserRegisterDto
import mikufan.cx.conduit.common.UserReq
import mikufan.cx.conduit.common.UserRsp
import org.http4k.core.Body
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.KotlinxSerialization.auto

val errorRspLens = Body.auto<ErrorRsp>().toLens()
fun createErrorRsp(vararg errors: String, status: Status) = errorRspLens(ErrorRsp(*errors), Response(status))


val userRegistrationLen = Body.auto<UserReq<UserRegisterDto>>().toLens()
val userRspLens = Body.auto<UserRsp>().toLens()
