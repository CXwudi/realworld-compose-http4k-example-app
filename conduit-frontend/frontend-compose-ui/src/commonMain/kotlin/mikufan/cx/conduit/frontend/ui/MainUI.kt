package mikufan.cx.conduit.frontend.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import coil3.svg.SvgDecoder
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
  setSingletonImageLoaderFactory { context ->
    ImageLoader.Builder(context)
      .components {
        add(SvgDecoder.Factory())
      }
      .crossfade(true)
      .build()
  }
  KoinContext(context = koin) { // currently unused, but added in case if we need it
    SetupUI {
      Surface(Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        // no windows padding adding here, all child UI please add ur own padding
      ) {
        RootNavigation(rootComponent)
      }
    }
  }
}

