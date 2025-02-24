package mikufan.cx.conduit.backend.db.repo

import mikufan.cx.conduit.backend.db.TransactionManager
import mikufan.cx.conduit.common.UserRegisterDto
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserRepoTest : KoinTest {

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(testDbConfig)
    }

    private val txManager: TransactionManager by inject()
    private val userRepo: UserRepo by inject()

    @Test
    fun `should insert new user`() {
        val userRegisterDto = UserRegisterDto("email@email.com", "new user", "password")

        txManager.tx {
            val newUser = userRepo.insert(userRegisterDto)
            assertNotNull(newUser)
            assertEquals(userRegisterDto.email, newUser.email)
            assertEquals(userRegisterDto.username, newUser.username)

            rollback()
        }
    }
}
