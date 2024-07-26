package mikufan.cx.conduit.frontend.logic.component.main.auth

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import mikufan.cx.conduit.frontend.logic.component.util.LocalKoinComponent
import mikufan.cx.conduit.frontend.logic.component.util.MviComponent
import mikufan.cx.conduit.frontend.logic.component.util.StoreBasedMviComponent

interface AuthPageComponent : MviComponent<AuthPageIntent, AuthPageState>

class DefaultAuthPageComponent(
  componentContext: ComponentContext,
  private val koin: LocalKoinComponent,
  authPageStoreFactory: AuthPageStoreFactory,
) : AuthPageComponent, StoreBasedMviComponent<AuthPageIntent, AuthPageState, Unit>(componentContext) {

  override val store = instanceKeeper.getStore { authPageStoreFactory.createStore() }
}