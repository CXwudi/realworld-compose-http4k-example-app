package mikufan.cx.conduit.frontend.app.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.startup.AppInitializer
import com.arkivanov.decompose.defaultComponentContext
import mikufan.cx.conduit.frontend.logic.component.DefaultRootNavComponent
import mikufan.cx.conduit.frontend.logic.component.util.toLocalKoinComponent
import mikufan.cx.conduit.frontend.ui.MainUI
import org.koin.androix.startup.KoinInitializer
import org.lighthousegames.logging.logging


class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    log.i { "onCreate" }

    val defaultComponentContext = defaultComponentContext()

    // this will retrieve the Koin instance cached in app initializer
    // see comments in https://stackoverflow.com/a/62395631/8529009
    val koin = AppInitializer.getInstance(application)
      .initializeComponent(KoinInitializer::class.java)

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
