package mikufan.cx.conduit.frontend.ui.screen.main.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import mikufan.cx.conduit.frontend.logic.component.main.auth.AuthPageComponent
import mikufan.cx.conduit.frontend.logic.component.main.auth.AuthPageIntent
import mikufan.cx.conduit.frontend.logic.component.main.auth.AuthPageMode
import mikufan.cx.conduit.frontend.ui.common.PasswordTextField
import mikufan.cx.conduit.frontend.ui.theme.LocalSpace

@Composable
fun AuthPage(component: AuthPageComponent, modifier: Modifier = Modifier) {
  val state by component.state.collectAsState()
  val isRegisterMode = remember { derivedStateOf { state.mode == AuthPageMode.REGISTER } }

  val email = remember { derivedStateOf { state.email } }
  val username = remember { derivedStateOf { state.username } }
  val password = remember { derivedStateOf { state.password } }

  val paddingLarge = LocalSpace.current.vertical.paddingLarge
  Box(
    modifier = modifier
      .fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    val verticalPadding = LocalSpace.current.vertical.paddingLarge * 4
    val horizontalPadding = LocalSpace.current.horizontal.padding
    Column(
      modifier = modifier
        .fillMaxHeight()
        .widthIn(max = LocalSpace.current.horizontal.maxContentSpace)
        .verticalScroll(rememberScrollState())
        .padding(vertical = verticalPadding)
        .safeDrawingPadding()
        .imePadding(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(
        space = LocalSpace.current.vertical.spacingLarge * 4,
        alignment = Alignment.CenterVertically
      ),
    ) {
      EmailTextField(
        emailProvider = email,
        onEmailChanged = { component.send(AuthPageIntent.EmailChanged(it)) },
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = horizontalPadding)
      )

      AnimatedVisibility(
        visible = isRegisterMode.value,
//        enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
//        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
      ) {

        OutlinedTextField(
          value = username.value,
          onValueChange = { component.send(AuthPageIntent.UsernameChanged(it)) },
          label = { Text("Username") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        )
      }


      PasswordTextField(
        passwordProvider = password,
        onPasswordChanged = { component.send(AuthPageIntent.PasswordChanged(it)) },
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = horizontalPadding),
      )

      Spacer(
        modifier = Modifier.height(paddingLarge * 2)
      )

      Button(
        onClick = { component.send(AuthPageIntent.AuthAction) },
      ) {
        AnimatedText(
          isRegisterMode = isRegisterMode,
          textForLoginMode = "Login",
          textForRegisterMode = "Register"
        )
      }

      SwitchModeRow(
        isRegisterMode = isRegisterMode,
        onChangeUrlClick = { component.send(AuthPageIntent.BackToLanding) },
        onSwitchModeClick = { component.send(AuthPageIntent.SwitchMode) },
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = horizontalPadding)
      )
    }
  }
}

@Composable
private fun EmailTextField(
  emailProvider: State<String>,
  onEmailChanged: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  OutlinedTextField(
    value = emailProvider.value,
    onValueChange = onEmailChanged,
    label = { Text("Email") },
    singleLine = true,
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
    modifier = modifier.fillMaxWidth(),
  )
}

@Composable
private fun SwitchModeRow(
  isRegisterMode: State<Boolean>,
  onChangeUrlClick: () -> Unit,
  onSwitchModeClick: () -> Unit,
  modifier: Modifier = Modifier,
) = Row(
  modifier = modifier,
  horizontalArrangement = Arrangement.SpaceBetween,
) {
  TextButton(onClick = onChangeUrlClick) {
    Text("Change URL")
  }
  TextButton(onClick = onSwitchModeClick) {
    AnimatedText(
      isRegisterMode = isRegisterMode,
      textForLoginMode = "To Register",
      textForRegisterMode = "To Login"
    )
  }
}

@Composable
private fun AnimatedText(
  isRegisterMode: State<Boolean>,
  textForLoginMode: String,
  textForRegisterMode: String,
  modifier: Modifier = Modifier,
) {
  val text =
    remember { derivedStateOf { if (isRegisterMode.value) textForRegisterMode else textForLoginMode } }
  AnimatedContent(
    targetState = text.value,
//    transitionSpec = { fadeInAndOut() },
  ) {
    Text(it, modifier = modifier)
  }
}
