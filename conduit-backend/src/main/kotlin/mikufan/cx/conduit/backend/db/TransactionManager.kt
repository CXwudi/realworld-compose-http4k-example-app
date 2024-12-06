package mikufan.cx.conduit.backend.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction

interface TransactionManager {
  fun <T> tx(block: Transaction.() -> T): T
}

class TransactionManagerImpl(
  val db: Database
) : TransactionManager {
  override fun <T> tx(block: Transaction.() -> T): T = transaction(db) {
    block()
  }
}