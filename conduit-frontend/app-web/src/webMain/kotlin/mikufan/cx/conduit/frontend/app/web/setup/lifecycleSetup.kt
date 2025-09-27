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

/**
 * Attaches the lifecycle of this `LifecycleRegistry` to the document's visibility state.
 *
 * This method observes the `document.visibilityState` and adjusts the lifecycle state
 * of the registry accordingly:
 * - Calls `resume` if the document is visible.
 * - Calls `stop` if the document is hidden.
 *
 * It sets an `onvisibilitychange` event handler on the document to monitor visibility state changes
 * and updates the lifecycle state in real time.
 *
 * Copied and adapted from
 * https://github.com/arkivanov/Decompose/blob/master/sample/app-js-compose/src/jsMain/kotlin/com/arkivanov/decompose/sample/app/Main.kt
 *
 */
fun LifecycleRegistry.attachToDocument() {
  fun onVisibilityChanged() {
    if (document.visibilityState == DocumentVisibilityState.visible) {
      resume()
    } else {
      stop()
    }
  }
  document.onvisibilitychange = EventHandler { onVisibilityChanged() }
  onVisibilityChanged()
}

/**
 * Launches an application within the lifecycle scope and manages its lifecycle events.
 * Cancels the associated coroutine scope when the lifecycle is destroyed.
 *
 * @param appLaunch A suspend lambda that contains the main application logic to be executed within a coroutine.
 */
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