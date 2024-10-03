package mikufan.cx.conduit.frontend.logic.component.util

import com.arkivanov.decompose.Cancellation
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.rx.observer
import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory

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

/**
 * Create a new store without Message. Or in another word Message type is same as State type.
 *
 * So the executor will be in charge to modify the state directly by calling `dispatch(newState)`.
 */
@Deprecated("It is not a good idea to let messahe type same as state type, since executor can execute on any thread but reducer is only running on main thread")
fun <Intent : Any, Action : Any, State : Any, Label : Any> StoreFactory.createWithoutMsg(
  name: String? = null,
  autoInit: Boolean = true,
  initialState: State,
  bootstrapper: Bootstrapper<Action>? = null,
  executorFactory: () -> Executor<Intent, Action, State, State, Label>,
  reducer: Reducer<State, State> = Reducer { it } // New default reducer that passes message as state
): Store<Intent, State, Label> {
  return this.create(
    name = name,
    autoInit = autoInit,
    initialState = initialState,
    bootstrapper = bootstrapper,
    executorFactory = executorFactory,
    reducer = reducer
  )
}