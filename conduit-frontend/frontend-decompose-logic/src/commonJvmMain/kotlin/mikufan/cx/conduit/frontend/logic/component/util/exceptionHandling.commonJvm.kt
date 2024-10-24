package mikufan.cx.conduit.frontend.logic.component.util

actual inline fun <reified T : Throwable> rethrowIfShouldNotBeHandled(
  throwable: T,
  block: (T) -> Unit
) {
  when (throwable) {
    is InterruptedException -> throw throwable
    is Exception -> block(throwable)
    else -> throw throwable
  }
}