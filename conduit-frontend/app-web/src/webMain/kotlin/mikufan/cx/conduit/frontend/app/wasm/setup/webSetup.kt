package mikufan.cx.conduit.frontend.app.wasm.setup


import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import kotlinx.browser.window
import web.dom.DocumentVisibilityState
import web.dom.Element
import web.dom.document
import web.dom.visible
import web.events.EventType
import web.events.addEventListener

fun setupBrowserUI() {
  document.body.apply {
    setAttribute("width", "${window.innerWidth}")
    setAttribute("height", "${window.innerHeight}")
  }
}

fun afterComposeSetup(newTitle: String) {
  val titleElement: Element = document.head.getElementsByTagName("title")[0]
  titleElement.textContent = newTitle
}

fun LifecycleRegistry.attachToDocument() {
  fun onVisibilityChanged() {
    if (document == DocumentVisibilityState.visible) {
      resume()
    } else {
      stop()
    }
  }

  onVisibilityChanged()

  document.addEventListener(type = EventType("visibilitychange"), handler = { onVisibilityChanged() })
}