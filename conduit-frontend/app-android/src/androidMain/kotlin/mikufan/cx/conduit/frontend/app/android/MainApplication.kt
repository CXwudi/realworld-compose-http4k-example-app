package mikufan.cx.conduit.frontend.app.android

import io.github.oshai.kotlinlogging.KotlinLogging
import mikufan.cx.conduit.frontend.logic.allModules
import org.koin.android.ext.koin.androidContext
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI

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
    log.info { "onCreate" }
  }

}

private val log = KotlinLogging.logger { }