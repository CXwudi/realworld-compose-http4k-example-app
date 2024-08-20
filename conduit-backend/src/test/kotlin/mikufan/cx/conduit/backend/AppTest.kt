package mikufan.cx.conduit.backend

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.koin.KoinExtension
import io.kotest.koin.KoinLifecycleMode
import io.kotest.matchers.shouldBe
import org.koin.test.KoinTest
import org.koin.test.inject

/**
 * This is just a test to make sure the koin modules setup correctly
 */
class AppTest : ShouldSpec(), KoinTest {

  override fun extensions(): List<Extension> = listOf(KoinExtension(allModules, mode = KoinLifecycleMode.Root))

  private val bootstrap: Bootstrap by inject()

  init {
    context("koin modules") {
      should("load successfully") {
        shouldNotThrow<Exception> {
          bootstrap shouldBe bootstrap
        }
      }
    }
  }
}