package mikufan.cx.conduit.frontend.logic.repo

import mikufan.cx.conduit.frontend.logic.repo.kstore.kstoreModule
import mikufan.cx.conduit.frontend.logic.repo.ktor.createDefaultHttpClient
import mikufan.cx.conduit.frontend.logic.repo.ktor.createKtorfit
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repoModule = module {
  includes(kstoreModule)

  // Originally, we were planning to use a StateFlow<HttpClient> that reflect the user config changes,
  // but we are unsure which coroutine dispatcher to use for the flow,
  // it could be the root decompose component's dispatcher, but that would require us to pass the lifecycleOwner
  // to the Koin container, which is not ideal.
  // So we decided to just create a static all-in-one HttpClient,
  // and let all Ktorfit interfaces accept URL and token as parameters.
  // This also makes a better performance since we don't need to create a new HttpClient for each user config change.
  singleOf(::createDefaultHttpClient)
  singleOf(::createKtorfit)
}