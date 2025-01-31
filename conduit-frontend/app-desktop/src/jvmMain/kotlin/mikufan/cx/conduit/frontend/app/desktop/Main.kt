package mikufan.cx.conduit.frontend.app.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.lifecycle.LifecycleController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import io.github.oshai.kotlinlogging.KotlinLogging
import mikufan.cx.conduit.frontend.app.desktop.util.runOnUiThread
import mikufan.cx.conduit.frontend.logic.allModules
import mikufan.cx.conduit.frontend.logic.component.RootNavComponentFactory
import mikufan.cx.conduit.frontend.ui.setupAndStartMainUI
import org.koin.dsl.koinApplication

fun initKoin() = koinApplication {
  modules(allModules)
}

fun main(args: Array<String>) {
  val lifecycle = LifecycleRegistry()
  val defaultComponentContext = runOnUiThread {
    DefaultComponentContext(lifecycle = lifecycle)
  }
  val koin = initKoin().koin
  val rootComponent = runOnUiThread {
    koin.get<RootNavComponentFactory>().create(defaultComponentContext)
  }

  log.info { "Starting" }

  application {

    val windowState = rememberWindowState()
    LifecycleController(lifecycle, windowState)

    Window(
      onCloseRequest = {
        koin.close()
        exitApplication()
      },
      title = "Conduit Desktop",
    ) {
      setupAndStartMainUI(koin, rootComponent)
    }
  }
}

private val log = KotlinLogging.logger { }