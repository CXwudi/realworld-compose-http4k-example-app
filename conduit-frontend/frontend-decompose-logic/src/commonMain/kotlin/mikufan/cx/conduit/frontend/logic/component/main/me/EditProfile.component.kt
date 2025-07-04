package mikufan.cx.conduit.frontend.logic.component.main.me

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mikufan.cx.conduit.frontend.logic.component.util.MviComponent

/**
 * Component interface for the edit profile screen that handles user profile editing functionality.
 * Implements the MVI pattern through [MviComponent] with [EditProfileIntent] and [EditProfileState].
 */
interface EditProfileComponent : MviComponent<EditProfileIntent, EditProfileState>

/**
 * Default implementation of [EditProfileComponent] that manages the edit profile screen's state and behavior.
 *
 * @param initialState The initial state of the edit profile form
 * @param componentContext The Decompose component context for lifecycle management
 * @param editProfileStoreFactory Factory to create the MVI store for profile editing
 * @param onSaveSuccess Callback function invoked when profile updates are successfully saved
 */
class DefaultEditProfileComponent(
  initialState: EditProfileState,
  componentContext: ComponentContext,
  editProfileStoreFactory: EditProfileStoreFactory,
  private val onSaveSuccess: () -> Unit,
  private val onBackWithoutSave: () -> Unit,
) : EditProfileComponent, ComponentContext by componentContext {

  private val store = instanceKeeper.getStore { editProfileStoreFactory.createStore(initialState) }

  override val state: StateFlow<EditProfileState> = store.stateFlow(coroutineScope())

  init {
    coroutineScope().launch {
      store.labels.collect {
        when (it) {
          is EditProfileLabel.SaveSuccessLabel -> onSaveSuccess()
          is EditProfileLabel.BackWithoutSave -> onBackWithoutSave()
          is EditProfileLabel.Unit -> Unit // do nothing as this label is just for test purpose
        }
      }
    }
  }

  override fun send(intent: EditProfileIntent) = store.accept(intent)

}

class EditProfileComponentFactory(
  private val editProfileStoreFactory: EditProfileStoreFactory,
) {
  fun create(
    componentContext: ComponentContext,
    loadedMe: LoadedMe,
    onSaveSuccess: () -> Unit,
    onBackWithoutSave: () -> Unit,
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
    onBackWithoutSave = onBackWithoutSave,
  )
}
