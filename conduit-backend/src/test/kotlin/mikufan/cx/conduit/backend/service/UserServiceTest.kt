package mikufan.cx.conduit.backend.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import mikufan.cx.conduit.backend.db.TransactionManager
import mikufan.cx.conduit.backend.db.repo.UserRepo
import mikufan.cx.conduit.backend.util.ConduitException
import mikufan.cx.conduit.common.UserDto
import mikufan.cx.conduit.common.UserRegisterDto
import org.jetbrains.exposed.sql.Transaction

class UserServiceTest : ShouldSpec({

  // Create a mock of TransactionManager
  val txManager = mockk<TransactionManager>() {
    // Set up the behavior for the tx method
    every {
      tx(any<Transaction.() -> Any?>())
    } answers {
      // Call the lambda directly with a mocked Transaction
      firstArg<Transaction.() -> Any?>().invoke(mockk())
    }
  }



  context("user registration") {
    val UserRegisterDto = UserRegisterDto("new user", "email@email.com", "password")
    should("register user successfully") {
      val userRepo = mockk<UserRepo>() {
        every { getByEmail(any()) } returns null
        every { getByUsername(any()) } returns null
        every { insert(any()) } answers {
          val dto = firstArg<UserRegisterDto>()
          UserDto(dto.email, dto.username, "", "", null)
        }
      }
      val userService = UserService(txManager, userRepo)

      val registerUser = userService.registerUser(UserRegisterDto)
      registerUser shouldBe UserDto("new user", "email@email.com", "", "", null)
    }
    should("throw on duplicate user") {
      val userRepo = mockk<UserRepo>() {
        every { getByEmail(any()) } returns mockk()
        every { getByUsername(any()) } returns mockk()
      }

      val userService = UserService(txManager, userRepo)

      val conduitException = shouldThrow<ConduitException> {
        userService.registerUser(UserRegisterDto)
      }

      conduitException.message shouldContain "User already exists"
    }
  }

})
