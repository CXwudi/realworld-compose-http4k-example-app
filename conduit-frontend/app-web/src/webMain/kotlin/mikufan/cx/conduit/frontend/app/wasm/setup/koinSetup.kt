package mikufan.cx.conduit.frontend.app.wasm.setup

import mikufan.cx.conduit.frontend.logic.allModules
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication

suspend fun initKoin(): KoinApplication {
  val application = koinApplication {
    modules(allModules)
  }
  // uncommon to call suspend function to add new instance to koin
  //application.koin.declare()
  return application
}