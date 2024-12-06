package mikufan.cx.conduit.backend.util

import io.mockk.mockk
import mikufan.cx.conduit.backend.db.TransactionManager
import org.jetbrains.exposed.sql.Transaction

/**
 * A transaction manager that does not actually perform any transactions.
 * Useful for testing purposes.
 *
 * However, make sure the test code does not call any functions from [Transaction] or directly.
 */
object NoOpsTxManager : TransactionManager {
  override fun <T> tx(block: Transaction.() -> T): T = mockk<Transaction>().block()
}