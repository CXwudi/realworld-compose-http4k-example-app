package mikufan.cx.conduit.backend.service

import io.mockk.every
import io.mockk.mockk
import mikufan.cx.conduit.backend.db.User
import mikufan.cx.conduit.backend.db.repo.UserRepo
import mikufan.cx.conduit.backend.util.ConduitException
import mikufan.cx.conduit.backend.util.NoOpsTxManager
import mikufan.cx.conduit.common.UserRegisterDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UserServiceTest {

    private val txManager = NoOpsTxManager
    private val userRegisterDto = UserRegisterDto("new user", "email@email.com", "password")

    @Nested
    inner class UserRegistration {
        
        @Test
        fun `should register user successfully`() {
            val userRepo = mockk<UserRepo>().apply {
                every { getByEmail(any()) } returns null
                every { getByUsername(any()) } returns null
                every { insert(any()) } answers {
                    val dto = firstArg<UserRegisterDto>()
                    mockk<User> {
                        every { id } returns mockk { every { value } returns 1 }
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
            
            assertEquals(userRegisterDto.username, registerUser.username)
            assertEquals(userRegisterDto.email, registerUser.email)
        }

        @Test
        fun `should throw on duplicate user`() {
            val userRepo = mockk<UserRepo>().apply {
                every { getByEmail(any()) } returns mockk()
                every { getByUsername(any()) } returns mockk()
            }

            val userService = UserService(txManager, userRepo)

            val exception = assertThrows<ConduitException> {
                userService.registerUser(userRegisterDto)
            }

            assert(exception.message?.contains("User already exists") == true)
        }
    }
}
