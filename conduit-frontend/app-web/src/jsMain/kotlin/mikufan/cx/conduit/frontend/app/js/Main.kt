package mikufan.cx.conduit.frontend.app.js

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mikufan.cx.conduit.frontend.logic.allModules
import mikufan.cx.conduit.frontend.logic.component.RootNavComponentFactory
import mikufan.cx.conduit.frontend.ui.setupAndStartMainUI
import org.jetbrains.skiko.wasm.onWasmReady
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import web.dom.DocumentVisibilityState
import web.dom.document
import web.events.EventType
import web.events.addEventListener

suspend fun initKoin(): KoinApplication {
  val application = koinApplication {
    modules(allModules)
  }
  // uncommon to call suspend function to add new instance to koin
  //application.koin.declare()
  return application
}


@OptIn(DelicateCoroutinesApi::class, ExperimentalComposeUiApi::class)
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
    
    onWasmReady {
      val canvasElementId = "ConduitCanvas"
      setupBrowserUI()
      CanvasBasedWindow(title = "Conduit Web", canvasElementId = canvasElementId) {
        setupAndStartMainUI(koinApp, rootComponent)
      }
    }
  }

}

private fun LifecycleRegistry.attachToDocument() {
  fun onVisibilityChanged() {
    if (document.visibilityState == DocumentVisibilityState.visible) {
      resume()
    } else {
      stop()
    }
  }

  onVisibilityChanged()

  document.addEventListener(type = EventType("visibilitychange"), handler = { onVisibilityChanged() })
}

private val log = KotlinLogging.logger { }