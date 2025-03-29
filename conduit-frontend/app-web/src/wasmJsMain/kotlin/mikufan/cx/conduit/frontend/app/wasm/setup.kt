package mikufan.cx.conduit.frontend.app.wasm

import kotlinx.browser.document
import kotlinx.browser.window

fun setupBrowserUI() {
  document.body!!.apply {
    setAttribute("width", "${window.innerWidth}")
    setAttribute("height", "${window.innerHeight}")
  }
}