package mikufan.cx.conduit.frontend.logic.component.util

import io.ktor.utils.io.errors.PosixException

actual inline fun <reified T : Throwable> rethrowIfShouldNotBeHandled(
  throwable: T,
  block: (T) -> Unit
) {
  when (throwable) {
    is PosixException.InterruptedException -> throw throwable
    is Exception -> block(throwable)
    else -> throw throwable
  }
}