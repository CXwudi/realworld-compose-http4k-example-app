package mikufan.cx.conduit.frontend.logic.component

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import mikufan.cx.conduit.frontend.logic.component.landing.LandingPageComponentFactory
import mikufan.cx.conduit.frontend.logic.component.landing.LandingPageStoreFactory
import mikufan.cx.conduit.frontend.logic.component.main.MainNavComponentFactory
import mikufan.cx.conduit.frontend.logic.component.main.MainNavStoreFactory
import mikufan.cx.conduit.frontend.logic.component.main.auth.AuthPageComponentFactory
import mikufan.cx.conduit.frontend.logic.component.main.auth.AuthPageStoreFactory
import mikufan.cx.conduit.frontend.logic.component.main.me.MeNavComponentFactory
import mikufan.cx.conduit.frontend.logic.service.serviceModule
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Require [serviceModule]
 */
val storeModule = module {
//  single<StoreFactory> { LoggingStoreFactory(DefaultStoreFactory()) } // un-comment to debug, very verbose!
  single<StoreFactory> { DefaultStoreFactory() }
  // can't use singleOf on constructor with default parameters
  single { LandingPageStoreFactory(get(), get()) }
  single { MainNavStoreFactory(get(), get()) }
  single { AuthPageStoreFactory(get(), get()) }
}

/**
 * Require [storeModule]
 */
val componentFactoryModule = module {
  singleOf(::RootNavComponentFactory)
  singleOf(::MainNavComponentFactory)
  singleOf(::LandingPageComponentFactory)
  singleOf(::AuthPageComponentFactory)
  singleOf(::MeNavComponentFactory)
}

val decomposeViewModelModules = listOf(storeModule, componentFactoryModule)