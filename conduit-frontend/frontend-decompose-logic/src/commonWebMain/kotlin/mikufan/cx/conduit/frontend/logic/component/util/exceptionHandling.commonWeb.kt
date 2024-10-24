package mikufan.cx.conduit.frontend.logic.component.util

actual inline fun <reified T : Throwable> rethrowIfShouldNotBeHandled(
  throwable: T,
  block: (T) -> Unit
) {
  block(throwable)
}