package mikufan.cx.conduit.backend

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import kotlin.test.assertTrue

/**
 * This is just a test to make sure the koin modules setup correctly
 */
class AppTest : KoinTest {

  @JvmField
  @RegisterExtension
  val koinTestExtension = KoinTestExtension.create {
    modules(allModules)
  }

  private val bootstrap: Bootstrap by inject()

  @Test
  fun `koin modules should load successfully`() {
    // if we get here without exception, the test passes
    // and verify bootstrap is injected
    assertTrue { bootstrap is Bootstrap }
  }
}
