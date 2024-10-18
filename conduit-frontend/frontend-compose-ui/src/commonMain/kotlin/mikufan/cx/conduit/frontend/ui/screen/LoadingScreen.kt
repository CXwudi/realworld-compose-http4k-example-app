package mikufan.cx.conduit.frontend.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import mikufan.cx.conduit.frontend.ui.theme.LocalSpace


@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
  val dotState = remember { mutableStateOf(1) }
  LaunchedEffect(Unit) {
    while (true) {
      dotState.value = dotState.value % 3 + 1
      delay(500L)
    }
  }
  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(LocalSpace.current.vertical.spacingLarge)
    ) {
      CircularProgressIndicator()
      Text("Loading${".".repeat(dotState.value)}", modifier = Modifier.animateContentSize())
    }
  }
}