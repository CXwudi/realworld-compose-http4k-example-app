package mikufan.cx.conduit.backend.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addEnvironmentSource
import com.sksamuel.hoplite.addResourceOrFileSource
import com.sksamuel.hoplite.sources.CommandLinePropertySource

fun loadConfig(args: Array<String>): Config = ConfigLoaderBuilder.default()
  .addPropertySource(CommandLinePropertySource(args, prefix =  "--", delimiter = "=")) // can handle empty array
  .addEnvironmentSource()
  .addResourceOrFileSource("/application-prod.yml", optional = true, allowEmpty = true)
  .addResourceOrFileSource("/application.yml", allowEmpty = true)
  .build()
  .loadConfigOrThrow<Config>()