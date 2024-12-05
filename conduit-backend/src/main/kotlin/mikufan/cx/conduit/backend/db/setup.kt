package mikufan.cx.conduit.backend.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import mikufan.cx.conduit.backend.config.DbConfig
import org.jetbrains.exposed.sql.Database
import javax.sql.DataSource

fun creatDataSource(dbConfig: DbConfig): DataSource {
  val config = HikariConfig().apply {
    jdbcUrl = dbConfig.url
    username = dbConfig.user
    password = dbConfig.password
    driverClassName = dbConfig.driver
  }
  val hikariDataSource = HikariDataSource(config)
  Runtime.getRuntime().addShutdownHook(Thread {
    hikariDataSource.close()
  })
  return hikariDataSource
}

fun createExposedDb(dataSource: DataSource) : Database = Database.connect(dataSource)

fun createConduitTransactionManager(db: Database) : TransactionManager = TransactionManagerImpl(db)
