package mikufan.cx.conduit.frontend.ui.screen.main.me

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.compose.rememberConstraintsSizeResolver
import coil3.request.ImageRequest
import mikufan.cx.conduit.frontend.logic.component.main.me.MePageComponent
import mikufan.cx.conduit.frontend.logic.component.main.me.MePageIntent
import mikufan.cx.conduit.frontend.logic.component.main.me.MePageState
import mikufan.cx.conduit.frontend.ui.theme.LocalSpace
import mikufan.cx.conduit.frontend.ui.util.LocalWindowAdaptiveInfo

@Composable
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

  val model by mePageComponent.state.collectAsState()


  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(LocalSpace.current.vertical.spacingLarge),
    modifier = modifier
      .fillMaxSize()
      .padding(bottom = paddingValues.calculateBottomPadding())
  ) {
    Spacer(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars))
    when (model) {
      is MePageState.Loading -> {
        CircularProgressIndicator()
      }
      is MePageState.Error -> {
        val errMsg = (model as MePageState.Error).errorMsg
        Text(text = "Failed to load user: $errMsg")
      }
      is MePageState.Loaded -> {
        val model = (model as MePageState.Loaded)
        Profile(
          username = model.username,
          bio = model.bio,
          imageUrl = model.imageUrl
        )
      }
    }

    HorizontalDivider()

    Text("Some text")

  }
}

@Composable
private fun Profile(
  username: String,
  bio: String,
  imageUrl: String,
  modifier: Modifier = Modifier
) {
  val widthClass = LocalWindowAdaptiveInfo.current.windowSizeClass.windowWidthSizeClass
  val shouldExpand by remember { derivedStateOf { widthClass != WindowWidthSizeClass.COMPACT } }


  if (shouldExpand) {
    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
      ProfileImage(imageUrl = imageUrl, username = username)
      Spacer(modifier = Modifier.width(24.dp))
      Column(
        verticalArrangement = Arrangement.spacedBy(LocalSpace.current.vertical.spacing * 2),
      ) {
        Text(
          text = username,
          style = MaterialTheme.typography.headlineMedium
        )
        Text(
          text = bio,
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  } else {
    Column(
      verticalArrangement = Arrangement.spacedBy(LocalSpace.current.vertical.spacing * 2),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      ProfileImage(imageUrl = imageUrl, username = username)
      Spacer(modifier = Modifier.height(LocalSpace.current.vertical.spacing * 2)) // so this is doubled the spacing
      Text(
        text = username,
        style = MaterialTheme.typography.headlineMedium
      )
      Text(
        text = bio,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
      )
    }
  }
}

@Composable
private fun ProfileImage(
  imageUrl: String,
  username: String,
  modifier: Modifier = Modifier,
) {
  val sizeResolver = rememberConstraintsSizeResolver()
  val painter = rememberAsyncImagePainter(
    model = ImageRequest.Builder(LocalPlatformContext.current)
      .data(imageUrl)
      .size(sizeResolver)
      .build(),
  )

  Image(
    painter = painter,
    contentDescription = "Profile picture of $username",
    contentScale = ContentScale.Crop,
    modifier = modifier
      .then(sizeResolver)
      .size(120.dp)
      .clip(CircleShape)
  )

}
