package mikufan.cx.conduit.backend.controller

import mikufan.cx.conduit.common.*
import org.http4k.core.Body
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.KotlinxSerialization.auto

val errorRspLens = Body.auto<GenericErrorRsp>().toLens()
fun createErrorRsp(vararg errors: String, status: Status) = errorRspLens(ErrorRsp(*errors), Response(status))


val userRegistrationLen = Body.auto<UserReq<UserRegisterDto>>().toLens()
val userRspLens = Body.auto<UserRsp>().toLens()
