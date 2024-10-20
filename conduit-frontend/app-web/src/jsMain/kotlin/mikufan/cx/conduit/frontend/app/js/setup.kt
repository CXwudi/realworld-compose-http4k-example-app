package mikufan.cx.conduit.frontend.app.js

import kotlinx.browser.document
import kotlinx.browser.window

fun setupBrowserUI() {

  document.body!!.apply {
    setAttribute("width", "${window.innerWidth}")
    setAttribute("height", "${window.innerHeight}")
  }

  //  val titleElement = htmlHeadElement.getElementsByTagName("title").item(0)
  //    ?: document.createElement("title").also { htmlHeadElement.appendChild(it) }
  //  titleElement.textContent = title
}