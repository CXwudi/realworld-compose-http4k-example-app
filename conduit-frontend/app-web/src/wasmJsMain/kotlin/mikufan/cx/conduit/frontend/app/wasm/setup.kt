package mikufan.cx.conduit.frontend.app.wasm

import kotlinx.browser.document
import kotlinx.browser.window

fun setupBrowserUI(title: String) {
  val htmlHeadElement = document.head!!

  document.body!!.apply {
    setAttribute("width", "${window.innerWidth}")
    setAttribute("height", "${window.innerHeight}")
  }

  // WORKAROUND: ComposeWindow does not implement `setTitle(title)`
  val titleElement = htmlHeadElement.getElementsByTagName("title").item(0)
    ?: document.createElement("title").also { htmlHeadElement.appendChild(it) }
  titleElement.textContent = title
}