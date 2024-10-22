package mikufan.cx.conduit.frontend.logic.repo

import de.jensklingenberg.ktorfit.Ktorfit
import mikufan.cx.conduit.frontend.logic.repo.api.createAuthApi
import mikufan.cx.conduit.frontend.logic.repo.api.createDefaultHttpClient
import mikufan.cx.conduit.frontend.logic.repo.api.createKtorfit
import mikufan.cx.conduit.frontend.logic.repo.kstore.KStoreKey
import mikufan.cx.conduit.frontend.logic.repo.kstore.UserConfigKStore
import mikufan.cx.conduit.frontend.logic.repo.kstore.UserConfigKStoreImpl
import mikufan.cx.conduit.frontend.logic.repo.kstore.kstoreKmpModule
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module


val kstoreModule = module {
  includes(kstoreKmpModule)
  single<UserConfigKStore> { UserConfigKStoreImpl(get(named(KStoreKey.PERSISTED_CONFIG))) }
}

/**
 * Require [kstoreModule]
 */
val apiModule = module {
  // Originally, we were planning to use a StateFlow<HttpClient> that reflect the user config changes,
  // but we are unsure which coroutine dispatcher to use for the flow,
  // it could be the root decompose component's dispatcher, but that would require us to pass the lifecycleOwner
  // to the Koin container, which is not ideal.
  // So we decided to just create a static all-in-one HttpClient,
  // and let all Ktorfit interfaces accept URL and token as parameters.
  // This also makes a better performance since we don't need to create a new HttpClient for each user config change.
  singleOf(::createDefaultHttpClient)
  singleOf(::createKtorfit)
  single { get<Ktorfit>().createAuthApi() }
}

val repoModules = listOf(kstoreModule, apiModule)


