package mikufan.cx.conduit.frontend.ui.screen.main.me

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowWidthSizeClass
import mikufan.cx.conduit.frontend.logic.component.main.me.MePageComponent
import mikufan.cx.conduit.frontend.logic.component.main.me.MePageIntent
import mikufan.cx.conduit.frontend.ui.util.LocalWindowAdaptiveInfo

@Composable
// TODO: add component
fun MePage(mePageComponent: MePageComponent, modifier: Modifier = Modifier) {
  MePageScaffold(
    onButtonClick = { mePageComponent.send(MePageIntent.AddArticle) },
  ) { paddingValues ->
    MePageContent(mePageComponent, paddingValues)
  }
}

@Composable
private fun MePageScaffold(
  onButtonClick: () -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable (PaddingValues) -> Unit
) {
  Scaffold(
    floatingActionButton = {
      FloatingActionButton(onClick = onButtonClick) {
        Icon(Icons.Default.Add, contentDescription = "Write a new post")
      }
    },
    modifier = modifier,
    content = content
  )
}

@Composable
private fun MePageContent(
  mePageComponent: MePageComponent,
  paddingValues: PaddingValues,
  modifier: Modifier = Modifier,
) {

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(bottom = paddingValues.calculateBottomPadding())
  ) {
    Spacer(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars))
    Text("Me TODO")
  }
}

@Composable
private fun Profile(
  // TODO
) {
  val widthClass = LocalWindowAdaptiveInfo.current.windowSizeClass.windowWidthSizeClass
  val shouldExpand by remember { derivedStateOf { widthClass != WindowWidthSizeClass.COMPACT } }
  if (shouldExpand) {
    Row {

    }
  } else {
    Column {

    }
  }
}