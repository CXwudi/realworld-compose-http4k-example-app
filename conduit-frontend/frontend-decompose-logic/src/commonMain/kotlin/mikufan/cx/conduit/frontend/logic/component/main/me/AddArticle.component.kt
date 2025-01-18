package mikufan.cx.conduit.frontend.logic.component.main.me

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mikufan.cx.conduit.frontend.logic.component.util.MviComponent


interface AddArticleComponent : MviComponent<AddArticleIntent, AddArticleState> {

}

class DefaultAddArticleComponent(
  componentContext: ComponentContext,
  addArticleStoreFactory: AddArticleStoreFactory,
  onBack: () -> Unit,
) : AddArticleComponent, ComponentContext by componentContext {

  private val store = instanceKeeper.getStore { addArticleStoreFactory.createStore() }

  override val state: StateFlow<AddArticleState> = store.stateFlow(coroutineScope())

  override fun send(intent: AddArticleIntent) = store.accept(intent)

  init {
    coroutineScope().launch {
      store.labels.collect {
        // so far there is no difference between [AddArticleLabel.BackWithoutPublish] and [AddArticleLabel.PublishSuccess]
        onBack()
      }
    }
  }
}


class AddArticleComponentFactory(
  private val addArticleStoreFactory: AddArticleStoreFactory,
) {
  fun create(componentContext: ComponentContext, onBack: () -> Unit) = DefaultAddArticleComponent(
    componentContext = componentContext,
    addArticleStoreFactory = addArticleStoreFactory,
    onBack = onBack,
  )
}