package mikufan.cx.conduit.frontend.logic.service

import mikufan.cx.conduit.frontend.logic.repo.repoModules
import mikufan.cx.conduit.frontend.logic.service.landing.LandingService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


/**
 * Require [repoModules]
 */
val serviceModule = module {
  singleOf(::LandingService)
}