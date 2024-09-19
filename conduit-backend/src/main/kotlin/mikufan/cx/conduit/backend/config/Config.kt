package mikufan.cx.conduit.backend.config

import org.http4k.core.Method

data class Config(
  val port: Int,
  val db: DbConfig,
  val cors: CorsConfig
)

data class DbConfig(
  val url: String,
  val user: String? = null,
  val password: String? = null,
  val driver: String,
)

data class CorsConfig(
  val allowedOrigins: List<String> = listOf("*"),
  val allowedMethods: List<Method> = Method.entries,
  val allowedHeaders: List<String> = listOf("*"),
  val allowCredentials: Boolean = true
)
