package mikufan.cx.conduit.frontend.logic.component

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import mikufan.cx.conduit.frontend.logic.component.landing.LandingPageStoreFactory
import mikufan.cx.conduit.frontend.logic.component.main.MainNavStoreFactory
import mikufan.cx.conduit.frontend.logic.component.main.auth.AuthPageStoreFactory
import mikufan.cx.conduit.frontend.logic.service.serviceModule
import org.koin.dsl.module

/**
 * Require [serviceModule]
 */
val storeModule = module {
  single<StoreFactory> { DefaultStoreFactory() }
  single { LandingPageStoreFactory(get(), get()) }
  single { MainNavStoreFactory(get(), get()) }
  single { AuthPageStoreFactory(get(), get()) }
}