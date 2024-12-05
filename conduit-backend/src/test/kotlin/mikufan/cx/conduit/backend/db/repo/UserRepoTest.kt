package mikufan.cx.conduit.backend.db.repo

import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.koin.KoinExtension
import io.kotest.koin.KoinLifecycleMode
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import mikufan.cx.conduit.backend.db.TransactionManager
import mikufan.cx.conduit.common.UserRegisterDto
import org.koin.test.KoinTest
import org.koin.test.inject

class UserRepoTest : ShouldSpec(), KoinTest {
  override fun extensions(): List<Extension> = listOf(KoinExtension(testDbConfig, mode = KoinLifecycleMode.Root))
  private val txManager: TransactionManager by inject()
  private val userRepo: UserRepo by inject()

  init {
    context("user repo") {
      should("insert new user") {
        val UserRegisterDto = UserRegisterDto("email@email.com", "new user", "password")

        txManager.tx {
          val newUser = userRepo.insert(UserRegisterDto)
          newUser shouldNotBe null
          newUser?.apply {
            email shouldBe UserRegisterDto.email
            username shouldBe UserRegisterDto.username
          }

          rollback()
        }

      }
    }
  }
}
