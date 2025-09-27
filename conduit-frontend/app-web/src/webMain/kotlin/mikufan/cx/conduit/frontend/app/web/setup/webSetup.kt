package mikufan.cx.conduit.frontend.app.web.setup


import kotlinx.browser.window
import web.dom.Element
import web.dom.document

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

