package mikufan.cx.conduit.frontend.logic.component.landing

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import mikufan.cx.conduit.frontend.logic.component.util.LocalKoinComponent
import mikufan.cx.conduit.frontend.logic.component.util.MviComponent
import mikufan.cx.conduit.frontend.logic.component.util.StoreBasedMviComponent

interface LandingPageComponent : MviComponent<LandingPageIntent, LandingPageState> {
}

class DefaultLandingPageComponent(
  componentContext: ComponentContext,
  private val koinComponent: LocalKoinComponent,
  storeFactory: LandingPageStoreFactory,
) : LandingPageComponent, StoreBasedMviComponent<LandingPageIntent, LandingPageState, LandingPageToNextPageLabel>(componentContext) {

  override val store = instanceKeeper.getStore {
    storeFactory.createStore()
  }

}
