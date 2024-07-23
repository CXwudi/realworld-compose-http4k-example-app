package mikufan.cx.conduit.frontend.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mikufan.cx.conduit.frontend.logic.component.DefaultRootNavComponent
import mikufan.cx.conduit.frontend.ui.screen.RootNavigation
import mikufan.cx.conduit.frontend.ui.util.SetupUI
import org.koin.compose.KoinContext
import org.koin.core.Koin

@Composable
fun MainUI(
  koin: Koin,
  rootComponent: DefaultRootNavComponent,
) {
  KoinContext(context = koin) { // currently unused, but added in case if we need it
    SetupUI {
      Surface(Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .windowInsetsPadding(WindowInsets.systemBars) // put system bar in the global UI, emi should be handled differently based on screen
      ) {
        RootNavigation(rootComponent)
      }
    }
  }
}

