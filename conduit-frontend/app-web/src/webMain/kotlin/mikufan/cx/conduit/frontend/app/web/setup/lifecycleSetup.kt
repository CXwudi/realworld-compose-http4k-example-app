package mikufan.cx.conduit.frontend.app.web.setup

import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import web.dom.DocumentVisibilityState
import web.dom.document
import web.dom.visible
import web.events.EventHandler

fun LifecycleRegistry.attachToDocument() {
  fun onVisibilityChanged() {
    if (document.visibilityState == DocumentVisibilityState.Companion.visible) {
      resume()
    } else {
      stop()
    }
  }
  document.onvisibilitychange = EventHandler { onVisibilityChanged() }
  onVisibilityChanged()
}

fun LifecycleRegistry.launchApp(appLaunch: suspend CoroutineScope.() -> Unit) {
  val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  appScope.launch {
    appLaunch()
  }

  doOnDestroy {
    log.info { "Shutting down" }
    appScope.cancel("App is destroyed")
  }
}

private val log = KotlinLogging.logger { }