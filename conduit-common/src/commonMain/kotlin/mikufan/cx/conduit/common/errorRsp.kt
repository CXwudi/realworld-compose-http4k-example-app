package mikufan.cx.conduit.common

import kotlinx.serialization.Serializable

@Serializable
data class ErrorRsp(
  val message: ErrorBody
)

@Serializable
data class ErrorBody(
  val body: List<String>
)

fun ErrorRsp(list: List<String>): ErrorRsp =
  ErrorRsp(ErrorBody(list))

fun ErrorRsp(vararg list: String): ErrorRsp =
  ErrorRsp(ErrorBody(list.toList()))
