package mikufan.cx.conduit.backend.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.oshai.kotlinlogging.KotlinLogging
import mikufan.cx.conduit.backend.db.TransactionManager
import mikufan.cx.conduit.backend.db.repo.UserRepo
import mikufan.cx.conduit.backend.util.ConduitException
import mikufan.cx.conduit.common.UserDto
import mikufan.cx.conduit.common.UserRegisterDto
import java.time.Instant
import java.time.temporal.ChronoUnit

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

    // Generate JWT token
    val token = JWT.create()
      .withSubject(newUser.id.value.toString())
      .withClaim("email", newUser.email)
      .withClaim("username", newUser.username)
      .withIssuedAt(Instant.now())
      .withExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
      .sign(Algorithm.HMAC256("your-secret-key")) // TODO: Move to configuration

    return UserDto(
      email = newUser.email,
      username = newUser.username,
      bio = newUser.bio,
      image = newUser.image,
      token = token
    )
  }
}

private val log = KotlinLogging.logger {}
