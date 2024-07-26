package mikufan.cx.conduit.frontend.ui.screen.main.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import mikufan.cx.conduit.frontend.logic.component.main.auth.AuthPageComponent
import mikufan.cx.conduit.frontend.logic.component.main.auth.AuthPageIntent
import mikufan.cx.conduit.frontend.logic.component.main.auth.AuthPageMode
import mikufan.cx.conduit.frontend.ui.theme.LocalSpace

@Composable
fun AuthPage(component: AuthPageComponent, modifier: Modifier = Modifier) {
  val state by component.state.collectAsState()
  val mode by remember { derivedStateOf { state.mode } }

  val paddingLarge = LocalSpace.current.vertical.paddingLarge
  Column(
    modifier = modifier.fillMaxSize().padding(paddingLarge * 4).verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    UsernameTextField(
      usernameProvider = { state.username },
      onUsernameChanged = { component.send(AuthPageIntent.UsernameChanged(it)) }
    )

    Spacer(modifier = Modifier.height(paddingLarge * 4))

    PasswordTextField(
      passwordProvider = { state.password },
      onPasswordChanged = { component.send(AuthPageIntent.PasswordChanged(it)) }
    )

    Spacer(modifier = Modifier.height(paddingLarge * 6))

    Button(
      onClick = { component.send(AuthPageIntent.AuthAction) },
      modifier = Modifier
    ) {
      Text(if (mode == AuthPageMode.SIGN_IN) "Login" else "Register")
    }

    Spacer(modifier = Modifier.height(paddingLarge * 4))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      TextButton(onClick = { component.send(AuthPageIntent.BackToLanding) }) {
        Text("Change URL")
      }
      TextButton(onClick = { component.send(AuthPageIntent.SwitchMode) }) {
        Text(if (mode == AuthPageMode.SIGN_IN) "To Register" else "To Login")
      }
    }
  }
}

@Composable
private fun UsernameTextField(
  usernameProvider: () -> String,
  onUsernameChanged: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  OutlinedTextField(
    value = usernameProvider(),
    onValueChange = onUsernameChanged,
    label = { Text("Username") },
    modifier = modifier.fillMaxWidth().imePadding()
  )
}

@Composable
private fun PasswordTextField(
  passwordProvider: () -> String,
  onPasswordChanged: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  OutlinedTextField(
    value = passwordProvider(),
    onValueChange = onPasswordChanged,
    label = { Text("Password") },
    visualTransformation = PasswordVisualTransformation(),
    modifier = modifier.fillMaxWidth().imePadding()
  )
}
