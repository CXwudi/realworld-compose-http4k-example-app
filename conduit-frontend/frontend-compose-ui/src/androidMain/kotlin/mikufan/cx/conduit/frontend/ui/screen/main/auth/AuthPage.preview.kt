package mikufan.cx.conduit.frontend.ui.screen.main.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import mikufan.cx.conduit.frontend.logic.component.main.auth.AuthPageComponent
import mikufan.cx.conduit.frontend.logic.component.main.auth.AuthPageIntent
import mikufan.cx.conduit.frontend.logic.component.main.auth.AuthPageMode
import mikufan.cx.conduit.frontend.logic.component.main.auth.AuthPageState
import mikufan.cx.conduit.frontend.ui.util.SetupUI

@Preview
@Composable
fun MainPageLoginPreview() {
  SetupUI {
    val fakeComponent = object : AuthPageComponent {
      override val state: StateFlow<AuthPageState> =
        MutableStateFlow(AuthPageState("my username", "my password", AuthPageMode.SIGN_IN))

      override fun send(intent: AuthPageIntent) {}
    }
    AuthPage(component = fakeComponent)
  }
}

@Preview
@Composable
fun MainPageRegisterPreview() {
  SetupUI {
    val fakeComponent = object : AuthPageComponent {
      override val state: StateFlow<AuthPageState> =
        MutableStateFlow(AuthPageState("my username", "my password", AuthPageMode.REGISTER))

      override fun send(intent: AuthPageIntent) {}
    }
    AuthPage(component = fakeComponent)
  }
}