package mikufan.cx.conduit.frontend.logic.component.landing

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import kotlinx.coroutines.flow.StateFlow
import mikufan.cx.conduit.frontend.logic.component.util.MviComponent

interface LandingPageComponent : MviComponent<LandingPageIntent, LandingPageState> {
}

class DefaultLandingPageComponent(
  componentContext: ComponentContext,
  storeFactory: LandingPageStoreFactory,
) : LandingPageComponent, ComponentContext by componentContext {

  private val store = instanceKeeper.getStore {
    storeFactory.createStore()
  }

  override val state: StateFlow<LandingPageState> = store.stateFlow(coroutineScope())

  override fun send(intent: LandingPageIntent) = store.accept(intent)
}


class LandingPageComponentFactory(
  private val storeFactory: LandingPageStoreFactory,
) {
  fun create(componentContext: ComponentContext) = DefaultLandingPageComponent(
    componentContext = componentContext,
    storeFactory = storeFactory,
  )
}