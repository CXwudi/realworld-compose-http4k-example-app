package mikufan.cx.conduit.backend.db.repo

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addEnvironmentSource
import com.sksamuel.hoplite.addResourceOrFileSource
import mikufan.cx.conduit.backend.config.Config
import mikufan.cx.conduit.backend.db.dbModule
import org.koin.dsl.module

val testDbConfig = module {
  single {
    ConfigLoaderBuilder.default()
      .addEnvironmentSource()
      .addResourceOrFileSource("/application-test.yml", optional = true, allowEmpty = true)
      .addResourceOrFileSource("/application.yml", allowEmpty = true)
      .build()
      .loadConfigOrThrow<Config>()
  }
  includes(dbModule)
}