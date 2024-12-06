package mikufan.cx.conduit.backend.controller

import mikufan.cx.conduit.backend.config.Config
import mikufan.cx.conduit.backend.controller.handler.RegisterUserHandler
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Helidon
import org.http4k.server.Http4kServer
import org.http4k.server.asServer

//// route setup ////

fun conduitRoute(apiRoute: RoutingHttpHandler): RoutingHttpHandler = routes(
  "/healthcheck" bind GET to { Response(Status.OK).body("OK") },
  "/api" bind apiRoute
)

fun apiRoute(
  registerUser: RegisterUserHandler,
) = routes(
  "/users" bind routes(
    "/" bind routes(
      POST to registerUser
    )
  )
)

enum class RouteEnum {
  ConduitAll,
  Api
}

//// server setup ////

fun conduitHttp4kHandler(
  metricFilter: Filter,
  loggingFilter: LoggingFilter,
  lenFailureFilter: Filter,
  conduitBusinessLogicExpFilter: ConduitBusinessLogicExceptionFilter,
  corsFilter: Filter,
  route: RoutingHttpHandler
): HttpHandler {
  return metricFilter
    .then(loggingFilter)
    .then(conduitBusinessLogicExpFilter)
    .then(lenFailureFilter)
    .then(corsFilter)
    .then(route)
}

typealias ConduitServer = Http4kServer

fun conduitServer(
  config: Config,
  handler: HttpHandler,
): ConduitServer = handler.asServer(Helidon(config.port))



