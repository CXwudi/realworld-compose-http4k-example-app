package mikufan.cx.conduit.frontend.app.wasm

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.browser.document
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mikufan.cx.conduit.frontend.logic.allModules
import mikufan.cx.conduit.frontend.logic.component.RootNavComponentFactory
import mikufan.cx.conduit.frontend.ui.setupAndStartMainUI
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import org.w3c.dom.Document

@OptIn(ExperimentalComposeUiApi::class, DelicateCoroutinesApi::class)
fun main(args: Array<String>) {

  val lifecycle = LifecycleRegistry()
  val defaultComponentContext = DefaultComponentContext(
    lifecycle = lifecycle
  )
  lifecycle.attachToDocument()

  GlobalScope.launch { // initialize within a global coroutine, workaround to calling suspend function from koin module
    // from https://github.com/InsertKoinIO/koin/issues/388#issuecomment-1195262422
    val koinApp = initKoin()
    val rootComponent = koinApp.koin.get<RootNavComponentFactory>().create(defaultComponentContext)

    log.info { "Starting" }

    setupBrowserUI()

    CanvasBasedWindow(title = "Conduit Web", canvasElementId = "ConduitCanvas") {
      setupAndStartMainUI(koinApp, rootComponent)
    }
  }

}

suspend fun initKoin(): KoinApplication {
  val application = koinApplication {
    modules(allModules)
  }
  // uncommon to call suspend function to add new instance to koin
  //application.koin.declare()
  return application
}


private fun LifecycleRegistry.attachToDocument() {
  fun onVisibilityChanged() {
    if (visibilityState(document) == "visible") {
      resume()
    } else {
      stop()
    }
  }

  onVisibilityChanged()

  document.addEventListener(type = "visibilitychange", callback = { onVisibilityChanged() })
}
// Workaround for Document#visibilityState not available in Wasm
@JsFun("(document) => document.visibilityState")
private external fun visibilityState(document: Document): String


private val log = KotlinLogging.logger { }