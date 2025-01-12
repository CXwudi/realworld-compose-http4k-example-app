package mikufan.cx.conduit.frontend.ui.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import mikufan.cx.conduit.frontend.ui.resources.Res
import mikufan.cx.conduit.frontend.ui.resources.eye_off_outline
import mikufan.cx.conduit.frontend.ui.resources.eye_outline
import mikufan.cx.conduit.frontend.ui.util.fadeInAndOut
import org.jetbrains.compose.resources.painterResource

@Composable
fun PasswordTextField(
  passwordProvider: State<String>,
  onPasswordChanged: (String) -> Unit,
  label: @Composable () -> Unit = { Text("Password") },
  modifier: Modifier = Modifier
) {
  var passwordVisibility by remember { mutableStateOf(true) }

  OutlinedTextField(
    value = passwordProvider.value,
    onValueChange = onPasswordChanged,
    label = label,
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
    modifier = modifier
  )
}