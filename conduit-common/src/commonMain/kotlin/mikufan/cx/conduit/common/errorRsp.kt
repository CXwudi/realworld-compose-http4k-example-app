package mikufan.cx.conduit.common

import kotlinx.serialization.Serializable

@Serializable
data class ErrorRsp(
  val errors: ErrorBody
) {
  val messagesAsString: String
    get() = if (errors.body.size == 1) errors.body.first() else errors.body.toString()
}

@Serializable
data class ErrorBody(
  val body: List<String>
)

fun ErrorRsp(list: List<String>): ErrorRsp =
  ErrorRsp(ErrorBody(list))

fun ErrorRsp(vararg list: String): ErrorRsp =
  ErrorRsp(ErrorBody(list.toList()))
