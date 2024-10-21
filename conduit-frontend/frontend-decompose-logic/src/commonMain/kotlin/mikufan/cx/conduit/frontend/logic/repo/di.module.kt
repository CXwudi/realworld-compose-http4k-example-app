package mikufan.cx.conduit.frontend.logic.repo

import mikufan.cx.conduit.frontend.logic.repo.kstore.kstoreModule
import mikufan.cx.conduit.frontend.logic.repo.ktor.createDefaultHttpClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repoModule = module {
  includes(kstoreModule)

  singleOf(::createDefaultHttpClient)
}