package mikufan.cx.conduit.backend.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import mikufan.cx.conduit.backend.db.User
import mikufan.cx.conduit.backend.db.repo.UserRepo
import mikufan.cx.conduit.backend.util.ConduitException
import mikufan.cx.conduit.backend.util.NoOpsTxManager
import mikufan.cx.conduit.common.UserDto
import mikufan.cx.conduit.common.UserRegisterDto

class UserServiceTest : ShouldSpec({

  val txManager = NoOpsTxManager

  context("user registration") {
    val userRegisterDto = UserRegisterDto("new user", "email@email.com", "password")
    should("register user successfully") {
      val userRepo = mockk<UserRepo>() {
        every { getByEmail(any()) } returns null
        every { getByUsername(any()) } returns null
        every { insert(any()) } answers {
          val dto = firstArg<UserRegisterDto>()
          mockk<User> {
            every { email } returns dto.email
            every { username } returns dto.username
            every { password } returns dto.password
            every { bio } returns ""
            every { image } returns ""
          }
        }
      }
      val userService = UserService(txManager, userRepo)

      val registerUser = userService.registerUser(userRegisterDto)
      registerUser shouldBe UserDto("new user", "email@email.com", "", "", null)
    }

    should("throw on duplicate user") {
      val userRepo = mockk<UserRepo>() {
        every { getByEmail(any()) } returns mockk()
        every { getByUsername(any()) } returns mockk()
      }

      val userService = UserService(txManager, userRepo)

      val conduitException = shouldThrow<ConduitException> {
        userService.registerUser(userRegisterDto)
      }

      conduitException.message shouldContain "User already exists"
    }
  }

})
