package mikufan.cx.conduit.frontend.ui.screen.main.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import mikufan.cx.conduit.frontend.logic.component.main.auth.AuthPageComponent
import mikufan.cx.conduit.frontend.logic.component.main.auth.AuthPageIntent
import mikufan.cx.conduit.frontend.logic.component.main.auth.AuthPageMode
import mikufan.cx.conduit.frontend.ui.resources.Res
import mikufan.cx.conduit.frontend.ui.resources.eye_off_outline
import mikufan.cx.conduit.frontend.ui.resources.eye_outline
import mikufan.cx.conduit.frontend.ui.theme.LocalSpace
import mikufan.cx.conduit.frontend.ui.util.fadeInAndOut
import org.jetbrains.compose.resources.painterResource

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
      // fill max size on outer box, then animate content size for inner column
      .fillMaxSize()
      .padding(paddingLarge * 4)
      .verticalScroll(rememberScrollState()),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = modifier,
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      EmailTextField(
        emailProvider = email,
        onEmailChanged = { component.send(AuthPageIntent.EmailChanged(it)) }
      )

      AnimatedVisibility(
        visible = isRegisterMode.value,
//        enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
//        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
      ) {
        Spacer(modifier = Modifier.height(0.dp).windowInsetsBottomHeight(WindowInsets.ime))
        
        OutlinedTextField(
          value = username.value,
          onValueChange = { component.send(AuthPageIntent.UsernameChanged(it)) },
          label = { Text("Username") },
          singleLine = true,
          modifier = Modifier.padding(top = paddingLarge * 4).fillMaxWidth(),
        )
      }

      Spacer(modifier = Modifier.height(paddingLarge * 4).windowInsetsBottomHeight(WindowInsets.ime))

      PasswordTextField(
        passwordProvider = password,
        onPasswordChanged = { component.send(AuthPageIntent.PasswordChanged(it)) },
      )

      Spacer(modifier = Modifier.height(paddingLarge * 6).windowInsetsBottomHeight(WindowInsets.ime))

      Button(
        onClick = { component.send(AuthPageIntent.AuthAction) },
      ) {
        AnimatedText(isRegisterMode = isRegisterMode, textForLoginMode = "Login", textForRegisterMode = "Register")
      }

      SwitchModeRow(
        isRegisterMode = isRegisterMode,
        onChangeUrlClick = { component.send(AuthPageIntent.BackToLanding) },
        onSwitchModeClick = { component.send(AuthPageIntent.SwitchMode) },
        modifier = Modifier.padding(top = paddingLarge * 4),
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
private fun PasswordTextField(
  passwordProvider: State<String>,
  onPasswordChanged: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var passwordVisibility by remember { mutableStateOf(true) }

  OutlinedTextField(
    value = passwordProvider.value,
    onValueChange = onPasswordChanged,
    label = {
      Text("Password")
    },
    trailingIcon = {
      IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
        AnimatedContent(
          targetState = passwordVisibility,
          transitionSpec = {
            fadeInAndOut()
          }
        ) {
          if (it) {
            Icon(painterResource(Res.drawable.eye_off_outline), "Hide")
          } else {
            Icon(painterResource(Res.drawable.eye_outline), "Show")
          }
        }
      }
    },
    visualTransformation = if (passwordVisibility) PasswordVisualTransformation() else VisualTransformation.None,
    singleLine = true,
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
    modifier = modifier.fillMaxWidth()
  )
}

@Composable
private fun SwitchModeRow(
  isRegisterMode: State<Boolean>,
  onChangeUrlClick: () -> Unit,
  onSwitchModeClick: () -> Unit,
  modifier: Modifier = Modifier,
) = Row(
  modifier = modifier.fillMaxWidth(),
  horizontalArrangement = Arrangement.SpaceBetween,
) {
  TextButton(onClick = onChangeUrlClick) {
    Text("Change URL")
  }
  TextButton(onClick = onSwitchModeClick) {
    AnimatedText(isRegisterMode = isRegisterMode, textForLoginMode = "To Register", textForRegisterMode = "To Login")
  }
}

@Composable
private fun AnimatedText(
  isRegisterMode: State<Boolean>,
  textForLoginMode: String,
  textForRegisterMode: String,
  modifier: Modifier = Modifier,
){
  val text = remember { derivedStateOf { if (isRegisterMode.value) textForRegisterMode else textForLoginMode } }
  AnimatedContent(
    targetState = text.value,
//    transitionSpec = { fadeInAndOut() },
  ) {
    Text(it, modifier = modifier)
  }
}
