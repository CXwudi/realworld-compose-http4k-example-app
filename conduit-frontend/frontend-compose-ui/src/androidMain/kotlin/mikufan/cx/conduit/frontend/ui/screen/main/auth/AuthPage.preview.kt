package mikufan.cx.conduit.frontend.ui.screen.main.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import mikufan.cx.conduit.frontend.logic.component.main.auth.AuthPageComponent
import mikufan.cx.conduit.frontend.logic.component.main.auth.AuthPageIntent
import mikufan.cx.conduit.frontend.logic.component.main.auth.AuthPageLabel
import mikufan.cx.conduit.frontend.logic.component.main.auth.AuthPageMode
import mikufan.cx.conduit.frontend.logic.component.main.auth.AuthPageState
import mikufan.cx.conduit.frontend.ui.util.SetupPreviewUI

@Preview
@Composable
fun MainPageLoginPreview() {
  SetupPreviewUI {
    val fakeComponent = object : AuthPageComponent {
      override val state: StateFlow<AuthPageState> =
        MutableStateFlow(AuthPageState("my username", "my password", "my email", AuthPageMode.SIGN_IN))

      override fun send(intent: AuthPageIntent) {}
      override val labels: Flow<AuthPageLabel> = flow {  }
    }
    AuthPage(component = fakeComponent)
  }
}

@Preview
@Composable
fun MainPageRegisterPreview() {
  SetupPreviewUI {
    val fakeComponent = object : AuthPageComponent {
      override val state: StateFlow<AuthPageState> =
        MutableStateFlow(AuthPageState("my username", "my password", "my email", AuthPageMode.REGISTER))

      override fun send(intent: AuthPageIntent) {}
      override val labels: Flow<AuthPageLabel> = flow {  }
    }
    AuthPage(component = fakeComponent)
  }
}