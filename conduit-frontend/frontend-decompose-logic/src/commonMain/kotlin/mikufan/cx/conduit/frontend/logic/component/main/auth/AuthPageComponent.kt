package mikufan.cx.conduit.frontend.logic.component.main.auth

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import kotlinx.coroutines.flow.StateFlow
import mikufan.cx.conduit.frontend.logic.component.util.MviComponent

interface AuthPageComponent : MviComponent<AuthPageIntent, AuthPageState>


class DefaultAuthPageComponent(
  componentContext: ComponentContext,
  authPageStoreFactory: AuthPageStoreFactory,
) : AuthPageComponent, ComponentContext by componentContext {

  private val store = instanceKeeper.getStore { authPageStoreFactory.createStore() }

  override val state: StateFlow<AuthPageState> = store.stateFlow(coroutineScope())

  override fun send(intent: AuthPageIntent) = store.accept(intent)
}


class AuthPageComponentFactory(
  private val authPageStoreFactory: AuthPageStoreFactory,
) {
  fun create(componentContext: ComponentContext) = DefaultAuthPageComponent(
    componentContext = componentContext,
    authPageStoreFactory = authPageStoreFactory,
  )
}