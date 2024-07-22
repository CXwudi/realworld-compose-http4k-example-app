package mikufan.cx.conduit.frontend.app.android

import mikufan.cx.conduit.frontend.logic.allModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import org.lighthousegames.logging.logging

class MainApplication : android.app.Application() {

  val koinApplication: KoinApplication by lazy {
    koinApplication {
      androidContext(this@MainApplication)
      modules(allModules)
    }
  }

  override fun onCreate() {
    super.onCreate()
    log.i { "onCreate" }
  }

}

private val log = logging()