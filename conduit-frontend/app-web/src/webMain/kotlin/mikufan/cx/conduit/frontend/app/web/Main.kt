package mikufan.cx.conduit.frontend.app.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.DelicateCoroutinesApi
import mikufan.cx.conduit.frontend.app.web.setup.afterComposeSetup
import mikufan.cx.conduit.frontend.app.web.setup.attachToDocument
import mikufan.cx.conduit.frontend.app.web.setup.initKoin
import mikufan.cx.conduit.frontend.app.web.setup.launchApp
import mikufan.cx.conduit.frontend.app.web.setup.setupBrowserUI
import mikufan.cx.conduit.frontend.logic.component.RootNavComponentFactory
import mikufan.cx.conduit.frontend.ui.setupAndStartMainUI

@OptIn(ExperimentalComposeUiApi::class, DelicateCoroutinesApi::class)
fun main(args: Array<String>) {

  val lifecycle = LifecycleRegistry()
  val defaultComponentContext = DefaultComponentContext(
    lifecycle = lifecycle
  )
  lifecycle.attachToDocument()

  lifecycle.launchApp {
    // initialize within a global coroutine, workaround to calling suspend function from koin module
    // from https://github.com/InsertKoinIO/koin/issues/388#issuecomment-1195262422
    val koinApp = initKoin()
    val rootComponent = koinApp.koin.get<RootNavComponentFactory>().create(defaultComponentContext)

    log.info { "Starting" }

    setupBrowserUI()

    ComposeViewport {
      afterComposeSetup(newTitle = "Conduit Web")
      setupAndStartMainUI(koinApp, rootComponent)
    }
  }

}


private val log = KotlinLogging.logger { }