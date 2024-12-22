package mikufan.cx.conduit.frontend.logic.component.main.me

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mikufan.cx.conduit.frontend.logic.component.util.MviComponent

interface EditProfileComponent : MviComponent<EditProfileIntent, EditProfileState>

class DefaultEditProfileComponent(
  initialState: EditProfileState,
  componentContext: ComponentContext,
  editProfileStoreFactory: EditProfileStoreFactory,
  private val onSaveSuccess: () -> Unit,
) : EditProfileComponent, ComponentContext by componentContext {

  private val store = instanceKeeper.getStore { editProfileStoreFactory.createStore(initialState) }

  override val state: StateFlow<EditProfileState> = store.stateFlow(coroutineScope())

  override fun send(intent: EditProfileIntent) = store.accept(intent)

  init {
    coroutineScope().launch {
      store.labels.collect {
        when (it) {
          is EditProfileLabel.SaveSuccessLabel -> onSaveSuccess()
          is EditProfileLabel.Unit -> Unit // do nothing as this label is just for test purpose
        }
      }
    }
  }

}

class EditProfileComponentFactory(
  private val editProfileStoreFactory: EditProfileStoreFactory,
) {
  fun create(
    componentContext: ComponentContext,
    loadedMe: LoadedMe,
    onSaveSuccess: () -> Unit,
  ): EditProfileComponent = DefaultEditProfileComponent(
    initialState = EditProfileState(
      email = loadedMe.email,
      username = loadedMe.username,
      bio = loadedMe.bio,
      imageUrl = loadedMe.imageUrl,
    ),
    componentContext = componentContext,
    editProfileStoreFactory = editProfileStoreFactory,
    onSaveSuccess = onSaveSuccess,
  )
}