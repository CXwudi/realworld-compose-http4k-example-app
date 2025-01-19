package mikufan.cx.conduit.frontend.ui.screen.main.me

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.compose.rememberConstraintsSizeResolver
import coil3.request.ImageRequest
import mikufan.cx.conduit.frontend.logic.component.main.me.MePageComponent
import mikufan.cx.conduit.frontend.logic.component.main.me.MePageIntent
import mikufan.cx.conduit.frontend.logic.component.main.me.MePageState
import mikufan.cx.conduit.frontend.ui.resources.Res
import mikufan.cx.conduit.frontend.ui.resources.outlined_broken_image
import mikufan.cx.conduit.frontend.ui.resources.outlined_hide_image
import mikufan.cx.conduit.frontend.ui.theme.LocalSpace
import org.jetbrains.compose.resources.painterResource

/**
 * The main composable for the Me page that displays the user's profile.
 *
 * @param mePageComponent The component that manages the Me page's state and logic
 * @param modifier Optional modifier for customizing the layout
 */
@Composable
fun MePage(mePageComponent: MePageComponent, modifier: Modifier = Modifier) {
  MePageScaffold(
    onButtonClick = { mePageComponent.send(MePageIntent.AddArticle) },
  ) { paddingValues ->
    MePageContent(mePageComponent, paddingValues)
  }
}

/**
 * A scaffold layout for the Me page that includes a floating action button for adding new articles.
 *
 * @param onButtonClick Callback invoked when the floating action button is clicked
 * @param modifier Optional modifier for customizing the layout
 * @param content The main content to display within the scaffold
 */
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

/**
 * The main content of the Me page that displays the user's profile information and action buttons.
 * Includes loading, error, and loaded states.
 *
 * @param mePageComponent The component that manages the Me page's state and logic
 * @param paddingValues Padding values to apply to the content
 * @param modifier Optional modifier for customizing the layout
 */
@Composable
private fun MePageContent(
  mePageComponent: MePageComponent,
  paddingValues: PaddingValues,
  modifier: Modifier = Modifier,
) {

  val model by mePageComponent.state.collectAsState()

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(LocalSpace.current.vertical.spacing),
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
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

    Spacer(modifier = Modifier.height(LocalSpace.current.vertical.spacingSmall))
    HorizontalDivider()
    Spacer(modifier = Modifier.height(LocalSpace.current.vertical.spacingSmall))

    val editProfileButtonEnabled by remember { derivedStateOf { model is MePageState.Loaded } }

    Button(
      onClick = { mePageComponent.send(MePageIntent.EditProfile) },
      enabled = editProfileButtonEnabled
    ) {
      Text("Edit Profile")
    }

    Button(onClick = { mePageComponent.send(MePageIntent.Logout) }) {
      Text("Logout")
    }

    Button(onClick = { mePageComponent.send(MePageIntent.SwitchServer) }) {
      Text("Switch Server")
    }
  }
}

/**
 * Displays the user's profile information including their image, username, and bio.
 * Adapts its layout based on the window width:
 * - In expanded mode: Shows a horizontal layout with image on the left and details on the right
 * - In compact mode: Shows a vertical layout with image on top and details below
 *
 * @param username The user's display name
 * @param bio The user's biography text
 * @param imageUrl URL of the user's profile image
 * @param modifier Optional modifier for customizing the layout
 */
@Composable
private fun Profile(
  username: String,
  bio: String,
  imageUrl: String,
  modifier: Modifier = Modifier
) {
  val widthClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
  val shouldExpand by remember(widthClass) { derivedStateOf { widthClass != WindowWidthSizeClass.COMPACT } }


  if (shouldExpand) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(LocalSpace.current.horizontal.spacing * 6)
    ) {
      ProfileImage(imageUrl = imageUrl, username = username)
      Column(
        verticalArrangement = Arrangement.spacedBy(LocalSpace.current.vertical.spacing * 2),
      ) {
        Text(
          text = username,
          style = MaterialTheme.typography.headlineMedium
        )
        ExpandableBioText(
          bio = bio,
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLength = when (widthClass) {
            WindowWidthSizeClass.EXPANDED -> 300
            WindowWidthSizeClass.MEDIUM -> 200
            else -> 100
          }
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
      ExpandableBioText(
        bio = bio,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        maxLength = when (widthClass) {
            WindowWidthSizeClass.EXPANDED -> 300
            WindowWidthSizeClass.MEDIUM -> 200
            else -> 100
        }
      )
    }
  }
}

/**
 * A text component that displays bio text with expand/collapse functionality.
 * Shows truncated text with a "Show more" button when the text exceeds [maxLength],
 * and provides smooth spring animation when expanding/collapsing.
 *
 * @param bio The biography text to display
 * @param style The text style to apply
 * @param color The text color
 * @param modifier Optional modifier for customizing the layout
 * @param textAlign Optional text alignment
 * @param maxLength Maximum number of characters to show before truncating
 */
@Composable
private fun ExpandableBioText(
  bio: String,
  style: TextStyle,
  color: Color,
  modifier: Modifier = Modifier,
  textAlign: TextAlign? = null,
  maxLength: Int
) {
  var isExpanded by remember { mutableStateOf(false) }
  val shouldTruncate = bio.length > maxLength
  val displayText = if (shouldTruncate && !isExpanded) {
    bio.trim().take(maxLength).trimEnd() + "..."
  } else {
    bio.trim()
  }

  Column(
    // we want the bio and the button to be closer
    verticalArrangement = Arrangement.spacedBy(-LocalSpace.current.vertical.spacingSmall),
    modifier = modifier
  ) {
    Text(
      text = displayText,
      style = style,
      color = color,
      textAlign = textAlign,
      modifier = Modifier.animateContentSize(
        animationSpec = spring(
          stiffness = Spring.StiffnessMedium,
          visibilityThreshold = IntSize.VisibilityThreshold,
        )
      )
    )
    if (shouldTruncate) {
      TextButton(
        onClick = { isExpanded = !isExpanded },
        contentPadding = PaddingValues(0.dp)
      ) {
        Text(
          text = if (isExpanded) "Collapse" else "Show more",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.primary
        )
      }
    }
  }
}

/**
 * Displays the user's profile image in a circular shape.
 * Uses Coil for image loading and applies proper content scaling.
 * Shows broken image icon if loading failed.
 * Shows hide image icon if image is not available.
 *
 * @param imageUrl URL of the profile image to load
 * @param username Username used for accessibility description
 * @param modifier Optional modifier for customizing the layout
 */
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

  val painterState by painter.state.collectAsState()

  when (painterState) {
    is AsyncImagePainter.State.Empty -> {
      Image(
        painter = painterResource(Res.drawable.outlined_hide_image),
        contentDescription = "No profile picture for $username",
        contentScale = ContentScale.Crop,
        modifier = modifier
          .then(sizeResolver)
          .size(120.dp)
          .clip(CircleShape)
      )
    }
    is AsyncImagePainter.State.Loading -> {
      CircularProgressIndicator(
        modifier = modifier
          .then(sizeResolver)
          .size(120.dp)
          .clip(CircleShape)
      )
    }
    is AsyncImagePainter.State.Success -> {
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
    is AsyncImagePainter.State.Error -> {
      Icon(
        painter = painterResource(Res.drawable.outlined_broken_image),
        contentDescription = "Broken profile picture of $username",
        modifier = modifier
          .then(sizeResolver)
          .size(120.dp)
          .clip(CircleShape)
      )
    }
  }


}
