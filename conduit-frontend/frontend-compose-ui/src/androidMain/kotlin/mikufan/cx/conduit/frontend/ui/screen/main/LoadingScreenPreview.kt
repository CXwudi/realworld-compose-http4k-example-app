package mikufan.cx.conduit.frontend.ui.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import mikufan.cx.conduit.frontend.ui.screen.LoadingScreen
import mikufan.cx.conduit.frontend.ui.util.SetupUI

@Composable
@Preview
fun LoadingScreenPreview() {
  SetupUI {
    LoadingScreen()
  }
}