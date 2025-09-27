package mikufan.cx.conduit.frontend.app.web.setup

import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.window
import web.dom.Element
import web.dom.document

fun setupBrowserUI() {
  document.body.apply {
    setAttribute("width", "${window.innerWidth}")
    setAttribute("height", "${window.innerHeight}")
  }
}

/**
 * A setup function that should be called when inside the [ComposeViewport]
 */
fun afterComposeSetup(newTitle: String) {
  val titleElement: Element = document.head.getElementsByTagName("title")[0]
  titleElement.textContent = newTitle
}

