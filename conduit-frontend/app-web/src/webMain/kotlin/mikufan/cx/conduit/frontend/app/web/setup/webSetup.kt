package mikufan.cx.conduit.frontend.app.web.setup

import androidx.compose.ui.window.ComposeViewport
import web.dom.Element
import web.dom.document

/**
 * A setup function that should be called when inside the [ComposeViewport]
 */
fun afterComposeSetup(newTitle: String) {
  document.title = newTitle
}

