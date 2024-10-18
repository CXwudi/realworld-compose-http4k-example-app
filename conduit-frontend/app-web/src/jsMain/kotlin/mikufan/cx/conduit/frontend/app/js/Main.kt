package mikufan.cx.conduit.frontend.app.js

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mikufan.cx.conduit.frontend.logic.allModules
import mikufan.cx.conduit.frontend.logic.component.RootNavComponentFactory
import mikufan.cx.conduit.frontend.ui.MainUI
import org.jetbrains.skiko.wasm.onWasmReady
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import org.lighthousegames.logging.logging
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


@OptIn(DelicateCoroutinesApi::class)
fun main(args: Array<String>) {

  val lifecycle = LifecycleRegistry()
  val defaultComponentContext = DefaultComponentContext(
    lifecycle = lifecycle
  )

  lifecycle.attachToDocument()
  GlobalScope.launch { // initialize within a global coroutine, workaround to calling suspend function from koin module
    // from https://github.com/InsertKoinIO/koin/issues/388#issuecomment-1195262422
    val koin = initKoin().koin
    val rootComponent = koin.get<RootNavComponentFactory>().create(defaultComponentContext)

    log.i { "Starting" }

    onWasmReady {
      BrowserViewportWindow(title = "Conduit Web", canvasElementId = "ConduitCanvas") {
        MainUI(koin, rootComponent)
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

private val log = logging()