package mikufan.cx.conduit.backend.controller

import mikufan.cx.conduit.backend.config.Config
import mikufan.cx.conduit.backend.config.configModule
import mikufan.cx.conduit.backend.controller.handler.RegisterUserHandler
import mikufan.cx.conduit.backend.service.serviceModule
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val filterModule = module {
  single(named(FilterEnum.Metrics)) { metricsFilter() }
  single(named(FilterEnum.Logging)) { LoggingFilter() }
  single(named(FilterEnum.Cors)) { corsFilter(config = get<Config>().cors) }
  single(named(FilterEnum.LensFailure)) { lensFailureFilter() }
  single(named(FilterEnum.ConduitBusinessLogicException)) { ConduitBusinessLogicExceptionFilter() }
}

/**
 * require [serviceModule]
 */
val handlerModule = module {
  singleOf(::RegisterUserHandler)

}

/**
 * require [handlerModule]
 */
val routeModule = module {
  single(named(RouteEnum.Api)) { apiRoute(registerUser = get()) }
  single(named(RouteEnum.ConduitAll)) { conduitRoute(apiRoute = get(named(RouteEnum.Api))) }
}

/**
 * require [configModule], [filterModule], [routeModule]
 */
val serverModule = module {
  single {
    conduitHttp4kHandler(
      metricFilter = get(named(FilterEnum.Metrics)),
      loggingFilter = get(named(FilterEnum.Logging)),
      lenFailureFilter = get(named(FilterEnum.LensFailure)),
      conduitBusinessLogicExpFilter = get(named(FilterEnum.ConduitBusinessLogicException)),
      corsFilter = get(named(FilterEnum.Cors)),
      route = get(named(RouteEnum.ConduitAll))
    )
  }

  single { conduitServer(config = get(), handler = get()) }
}

val allHttpSetupModule = module {
  includes(filterModule, handlerModule, routeModule, serverModule)
}
