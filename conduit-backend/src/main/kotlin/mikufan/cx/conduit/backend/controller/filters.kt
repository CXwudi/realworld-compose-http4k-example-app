package mikufan.cx.conduit.backend.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import mikufan.cx.conduit.backend.config.CorsConfig
import mikufan.cx.conduit.backend.util.ConduitException
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Status
import org.http4k.filter.*
import java.util.concurrent.Executors


fun metricsFilter(): Filter {
  return ServerFilters.MicrometerMetrics.RequestTimer(SimpleMeterRegistry())
}

class LoggingFilter : Filter {

  companion object {
    @JvmStatic
    private val log = KotlinLogging.logger {}
    private val logExecutor = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().name("logging-", 0L).factory())
  }

  override fun invoke(next: HttpHandler): HttpHandler = { req ->
    logExecutor.execute { log.info { "${req.method} ${req.uri}" } } // log request in background
    next(req)
  }

}

fun corsFilter(config: CorsConfig): Filter {
  val configOrigins = config.allowedOrigins
  val originPolicy = if (configOrigins.isEmpty() || configOrigins.contains("*")) {
    OriginPolicy.AllowAll()
  } else {
    OriginPolicy.AnyOf(configOrigins)
  }

  return ServerFilters.Cors(
    CorsPolicy(
      originPolicy = originPolicy,
      headers = config.allowedHeaders,
      methods = Method.entries,
      credentials = config.allowCredentials,
    )
  )
}

fun lensFailureFilter(): Filter {
  return ServerFilters.CatchLensFailure { err ->
    createErrorRsp(err.message ?: "Unknown error", status = Status.UNPROCESSABLE_ENTITY)
  }
}

class ConduitBusinessLogicExceptionFilter : Filter {

  companion object {
    @JvmStatic
    private val log = KotlinLogging.logger {}
  }

  override fun invoke(next: HttpHandler): HttpHandler = { req ->
    try {
      next(req)
    } catch (e: ConduitException) {
      log.error(e) { "Conduit business logic failure case" }
      createErrorRsp(e.message ?: "Unknown business logic", status = Status.BAD_REQUEST)
    } catch (e: Exception) {
      log.error(e) { "Unexpected exception of ${e::class.simpleName}" }
      createErrorRsp(e.message ?: "Unknown error", status = Status.INTERNAL_SERVER_ERROR)
    }
  }
}

enum class FilterEnum {
  Metrics,
  Logging,
  Cors,
  LensFailure,
  ConduitBusinessLogicException
}