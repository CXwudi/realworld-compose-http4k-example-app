package mikufan.cx.conduit.common

import kotlinx.serialization.Serializable

@Serializable
data class UserReq<T>(
  val user: T
)

@Serializable
data class UserRegisterDto(
  val email: String,
  val username: String,
  val password: String
)

@Serializable
data class UserLoginDto(
  val email: String,
  val password: String
)

@Serializable
data class UserUpdateDto(
  val email: String,
  val username: String,
  val image: String,
  val bio: String,
  val password: String?,
)

object UserDtoUtils {
  fun createLoginReq(email: String, password: String) = UserReq(
    UserLoginDto(
      email = email,
      password = password
    )
  )

  fun createRegisterReq(email: String, username: String, password: String) = UserReq(
    UserRegisterDto(
      email = email,
      username = username,
      password = password
    )
  )
}

@Serializable
data class UserRsp(
  val user: UserDto
)

/**
 * The user response DTO for login and register
 */
@Serializable
data class UserDto(
  val email: String,
  val username: String,
  val bio: String?,
  val image: String?,
  val token: String?
)

