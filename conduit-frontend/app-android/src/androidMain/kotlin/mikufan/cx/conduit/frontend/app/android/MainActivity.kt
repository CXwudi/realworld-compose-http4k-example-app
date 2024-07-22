package mikufan.cx.conduit.frontend.app.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.arkivanov.decompose.defaultComponentContext
import mikufan.cx.conduit.frontend.logic.component.DefaultRootNavComponent
import mikufan.cx.conduit.frontend.logic.component.util.toLocalKoinComponent
import mikufan.cx.conduit.frontend.ui.MainUI
import org.lighthousegames.logging.logging


class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    log.i { "onCreate" }

    val defaultComponentContext = defaultComponentContext()

    val mainApplication = application as MainApplication
    val koin = mainApplication.koinApplication.koin

    val rootComponent =
      DefaultRootNavComponent(defaultComponentContext, koin.toLocalKoinComponent(), koin.get())
    enableEdgeToEdge()
    setContent {
      MainUI(koin, rootComponent)
    }
  }


  override fun onDestroy() {
    super.onDestroy()
    log.i { "onDestroy" }
  }
}

private val log = logging()
