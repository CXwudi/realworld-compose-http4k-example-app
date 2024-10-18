package mikufan.cx.conduit.frontend.logic.component.util

import com.arkivanov.decompose.Cancellation
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.rx.observer
import com.arkivanov.mvikotlin.core.store.Store

/**
 * Convert the [Store.state] to a [Value]
 *
 * Causion: to expose it from [ComponentContext], do not use property delegation `get()`
 */
val <T : Any> Store<*, T, *>.stateValue: Value<T>
  get() = object : Value<T>() {
    override val value: T get() = state

    override fun subscribe(observer: (T) -> Unit): Cancellation {
      val disposable = states(observer(onNext = observer))

      return Cancellation {
        disposable.dispose()
      }
    }
  }
