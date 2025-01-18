package mikufan.cx.conduit.frontend.ui.screen.main.me

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import mikufan.cx.conduit.frontend.logic.component.main.me.EditProfileComponent
import mikufan.cx.conduit.frontend.logic.component.main.me.EditProfileIntent
import mikufan.cx.conduit.frontend.logic.component.main.me.EditProfileState
import mikufan.cx.conduit.frontend.ui.util.SetupPreviewUI


@Composable
@Preview
fun EditProfilePreview() {
  SetupPreviewUI {
    val mockEditProfileComponent = object : EditProfileComponent {
      override val state: StateFlow<EditProfileState> = MutableStateFlow(EditProfileState(
        email = "email",
        username = "username",
        bio = "bio",
        imageUrl = "imageUrl",
        password = "password2",
        errorMsg = "some error",
      ))

      override fun send(intent: EditProfileIntent) {}
    }
    EditProfilePage(mockEditProfileComponent)
  }
}