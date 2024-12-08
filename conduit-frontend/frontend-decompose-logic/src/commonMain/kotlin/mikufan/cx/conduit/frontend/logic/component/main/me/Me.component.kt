package mikufan.cx.conduit.frontend.logic.component.main.me

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import kotlinx.coroutines.flow.StateFlow
import mikufan.cx.conduit.frontend.logic.component.util.MviComponent

interface MePageComponent : MviComponent<MePageIntent, MePageState>

class DefaultMePageComponent(
  componentContext: ComponentContext,
  meStoreFactory: MeStoreFactory,
) : MePageComponent, ComponentContext by componentContext {

  private val store = instanceKeeper.getStore { meStoreFactory.createStore() }

  override val state: StateFlow<MePageState> = store.stateFlow(coroutineScope())

  override fun send(intent: MePageIntent) = store.accept(intent)
}

class MePageComponentFactory(
  private val meStoreFactory: MeStoreFactory,
) {
  fun create(componentContext: ComponentContext) = DefaultMePageComponent(
    componentContext = componentContext,
    meStoreFactory = meStoreFactory,
  )
}
