package mikufan.cx.conduit.frontend.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import mikufan.cx.conduit.frontend.logic.component.landing.LandingPageComponent
import mikufan.cx.conduit.frontend.logic.component.landing.LandingPageIntent
import mikufan.cx.conduit.frontend.logic.component.landing.LandingPageLabel
import mikufan.cx.conduit.frontend.ui.theme.LocalSpace

@Composable
fun LandingPage(component: LandingPageComponent, modifier: Modifier = Modifier) {

  val state by component.state.collectAsState()
  val urlText = remember { derivedStateOf { state.url } }

  val errorMsgState = remember { mutableStateOf("") }
  val showErrorAlert by remember { derivedStateOf { errorMsgState.value.isNotBlank() } }

  val scope = rememberCoroutineScope()
  scope.launch {
    component.labels.collect { label ->
      when (label) {
        is LandingPageLabel.Failure -> {
          errorMsgState.value = label.message
        }
        // Handle other labels if necessary
        else -> {}
      }
    }
  }

  showErrorAlert(showErrorAlert, errorMsgState)

  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier.fillMaxSize().windowInsetsPadding(WindowInsets.ime)
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(LocalSpace.current.vertical.spacingLarge * 2)
    ) {
      OutlinedTextField(
        value = urlText.value,
        label = { Text("URL") },
        onValueChange = { component.send(LandingPageIntent.TextChanged(it)) },
        singleLine = true,
      )
      Button(onClick = { component.send(LandingPageIntent.CheckAndMoveToMainPage) }) {
        Text("Connect")
      }
    }

  }
}

@Composable
fun showErrorAlert(showErrorAlert: Boolean, errorMsgState: MutableState<String>) {
  // almost not possible to animate it, as dialog are drawn outside of current tree
  // see https://github.com/JetBrains/compose-multiplatform/issues/4431
  if (showErrorAlert) {
    AlertDialog(
      onDismissRequest = { errorMsgState.value = "" },
      shape = MaterialTheme.shapes.large,
      tonalElevation = LocalSpace.current.vertical.spacingLarge,
      icon = {
        Icon(
          imageVector = Icons.Filled.Warning,
          contentDescription = "Error Icon",
          tint = MaterialTheme.colorScheme.error,
          modifier = Modifier.size(LocalSpace.current.vertical.spacingLarge * 4)
        )
      },
      title = {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "Error",
            style = MaterialTheme.typography.headlineSmall
          )
        }
      },
      text = {
        val scrollState = rememberScrollState()
        Text(
          text = errorMsgState.value,
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier
            .heightIn(max = 600.dp)
            .verticalScroll(scrollState)
        )
      },
      confirmButton = {
        TextButton(
          onClick = { errorMsgState.value = "" }
        ) {
          Text("OK")
        }
      },
    )
  }
}
