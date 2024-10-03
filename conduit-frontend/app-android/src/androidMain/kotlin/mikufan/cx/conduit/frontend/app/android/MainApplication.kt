package mikufan.cx.conduit.frontend.app.android

import mikufan.cx.conduit.frontend.logic.allModules
import org.koin.android.ext.koin.androidContext
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.lighthousegames.logging.logging

@KoinExperimentalAPI
class MainApplication : android.app.Application() {

  init {
    KoinStartup.onKoinStartup {
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