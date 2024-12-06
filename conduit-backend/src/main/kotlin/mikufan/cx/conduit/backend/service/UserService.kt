package mikufan.cx.conduit.backend.service

import mikufan.cx.conduit.backend.db.TransactionManager
import mikufan.cx.conduit.backend.db.repo.UserRepo
import mikufan.cx.conduit.backend.util.ConduitException
import mikufan.cx.conduit.common.UserDto
import mikufan.cx.conduit.common.UserRegisterDto
import mikufan.cx.inlinelogging.KInlineLogging

class UserService(
  private val txManager: TransactionManager,
  private val userRepo: UserRepo,
) {

  fun registerUser(user: UserRegisterDto): UserDto {
    val newUser = txManager.tx {
      val userDto = userRepo.getByUsername(user.username) ?: userRepo.getByEmail(user.email)
      if (userDto != null) {
        throw ConduitException("User already exists, username or email must be unique")
      } else {
        userRepo.insert(user)
      }
    }
    log.info { "Successfully created new user ${user.username}" }

    // TODO: set token
    return UserDto(
      email = newUser.email,
      username = newUser.username,
      bio = newUser.bio,
      image = newUser.image,
      token = null
    )
  }
}

private val log = KInlineLogging.logger()