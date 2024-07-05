package mikufan.cx.conduit.frontend.app.android

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.arkivanov.decompose.defaultComponentContext
import mikufan.cx.conduit.frontend.logic.component.DefaultRootNavComponent
import mikufan.cx.conduit.frontend.logic.util.toLocalKoinComponent


class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Log.i(this@MainActivity::class.simpleName, "onCreate")

    val defaultComponentContext = defaultComponentContext()

    val mainApplication = application as MainApplication

    val rootComponent =
      DefaultRootNavComponent(defaultComponentContext, mainApplication.koin.toLocalKoinComponent())
    setContent {

    }
  }


  override fun onDestroy() {
    super.onDestroy()
    Log.i(this@MainActivity::class.simpleName, "onDestroy")
  }
}
