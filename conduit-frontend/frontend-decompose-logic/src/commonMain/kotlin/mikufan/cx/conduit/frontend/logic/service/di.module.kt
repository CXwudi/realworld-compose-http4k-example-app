package mikufan.cx.conduit.frontend.logic.service

import mikufan.cx.conduit.frontend.logic.repo.repoModules
import mikufan.cx.conduit.frontend.logic.service.landing.DefaultLandingService
import mikufan.cx.conduit.frontend.logic.service.landing.LandingService
import mikufan.cx.conduit.frontend.logic.service.main.AuthService
import mikufan.cx.conduit.frontend.logic.service.main.DefaultAuthService
import mikufan.cx.conduit.frontend.logic.service.main.DefaultMePageService
import mikufan.cx.conduit.frontend.logic.service.main.MePageService
import org.koin.dsl.module


/**
 * Require [repoModules]
 */
val serviceModule = module {
  single<LandingService> { DefaultLandingService(get(), get()) }
  single<AuthService> { DefaultAuthService(get(), get()) }
  single<MePageService> { DefaultMePageService(get(), get()) }
}