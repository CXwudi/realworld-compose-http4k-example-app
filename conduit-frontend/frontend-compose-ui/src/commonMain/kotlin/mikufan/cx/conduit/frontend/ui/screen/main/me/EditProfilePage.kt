package mikufan.cx.conduit.frontend.ui.screen.main.me

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mikufan.cx.conduit.frontend.logic.component.main.me.EditProfileComponent
import mikufan.cx.conduit.frontend.logic.component.main.me.EditProfileIntent
import mikufan.cx.conduit.frontend.ui.common.PasswordTextField
import mikufan.cx.conduit.frontend.ui.common.layout.PageColumn
import mikufan.cx.conduit.frontend.ui.theme.LocalSpace

@Composable
fun EditProfilePage(editProfileComponent: EditProfileComponent, modifier: Modifier = Modifier) {
  val model by editProfileComponent.state.collectAsState()

  PageColumn(
    modifier = modifier
  ) {
    val username by remember { derivedStateOf { model.username } }
    val bio by remember { derivedStateOf { model.bio } }
    val imageUrl by remember { derivedStateOf { model.imageUrl } }
    val passwordState = remember { derivedStateOf { model.password } }
    val errorMsgState = remember { derivedStateOf { model.errorMsg } }

    IconButton(
      onClick = { editProfileComponent.send(EditProfileIntent.BackWithoutSave) },
      modifier = Modifier
        .align(Alignment.Start)
        .padding(horizontal = LocalSpace.current.horizontal.padding)
    ) {
      Icon(
        imageVector = Icons.AutoMirrored.Default.ArrowBack,
        contentDescription = "Go back"
      )
    }

    OutlinedTextField(
      value = username,
      onValueChange = { editProfileComponent.send(EditProfileIntent.UsernameChanged(it)) },
      label = { Text("Username") },
      singleLine = true,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = LocalSpace.current.horizontal.padding),
    )

    OutlinedTextField(
      value = bio,
      onValueChange = { editProfileComponent.send(EditProfileIntent.BioChanged(it)) },
      label = { Text("Bio") },
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = LocalSpace.current.horizontal.padding),
    )

    OutlinedTextField(
      value = imageUrl,
      onValueChange = { editProfileComponent.send(EditProfileIntent.ImageUrlChanged(it)) },
      label = { Text("Image URL") },
      singleLine = true,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = LocalSpace.current.horizontal.padding),
    )

    PasswordTextField(
      passwordProvider = passwordState,
      onPasswordChanged = { editProfileComponent.send(EditProfileIntent.PasswordChanged(it)) },
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = LocalSpace.current.horizontal.padding),
    )

    Button(
      onClick = { editProfileComponent.send(EditProfileIntent.Save) },
      modifier = Modifier.padding(horizontal = LocalSpace.current.horizontal.padding),
    ) {
      Text("Save")
    }

    val showErrorMsg = remember { derivedStateOf { model.errorMsg.isNotBlank() } }
    AnimatedVisibility(visible = showErrorMsg.value) {
      ErrorMessage(errorMsgState)
    }
  }
}

@Composable
private fun ErrorMessage(message: State<String>, modifier: Modifier = Modifier) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
  ) {
    Icon(
      imageVector = Icons.Filled.Warning,
      contentDescription = "Error",
      tint = androidx.compose.ui.graphics.Color.Red
    )
    Spacer(modifier = Modifier.width(LocalSpace.current.horizontal.spacing))
    Text(
      text = message.value,
      color = androidx.compose.ui.graphics.Color.Red
    )
  }
}
