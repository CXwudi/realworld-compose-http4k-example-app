package mikufan.cx.conduit.backend.db.repo

import io.mockk.mockk
import mikufan.cx.conduit.backend.config.Config
import mikufan.cx.conduit.backend.config.DbConfig
import mikufan.cx.conduit.backend.db.dbModule
import org.koin.dsl.module

val testDbConfig = module {
  single {
    Config(
      port = 0,
      cors = mockk(),
      db = DbConfig(url = "jdbc:postgresql://localhost:5432/conduit-db", user = "conduit-user", password = "conduit-password", driver = "org.postgresql.Driver")
    )
  }
  includes(dbModule)
}