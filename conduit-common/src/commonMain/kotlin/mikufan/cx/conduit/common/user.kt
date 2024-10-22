package mikufan.cx.conduit.common

import kotlinx.serialization.Serializable


@Serializable
data class NewUserReq(
  val user: NewUserDto
)

@Serializable
data class NewUserDto(
  val email: String,
  val username: String,
  val password: String
)

fun NewUserReq(
  email: String,
  username: String,
  password: String
) = NewUserReq(NewUserDto(email, username, password))



@Serializable
data class UserRsp(
  val user: UserDto
)

@Serializable
data class UserDto(
  val id: Int,
  val email: String,
  val username: String,
  val bio: String?,
  val image: String?,
  val token: String?
)

