package mikufan.cx.conduit.frontend.ui.screen.main.me

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mikufan.cx.conduit.frontend.logic.component.main.me.AddArticleComponent
import mikufan.cx.conduit.frontend.logic.component.main.me.AddArticleIntent
import mikufan.cx.conduit.frontend.ui.theme.LocalSpace

@Composable
fun AddArticlePage(addArticleComponent: AddArticleComponent, modifier: Modifier = Modifier) {
  val model by addArticleComponent.state.collectAsState()

  Box( // outer box to make sure the column is in the center
    modifier = modifier
      .fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(LocalSpace.current.vertical.spacing),
      modifier = modifier
        .widthIn(max = LocalSpace.current.horizontal.maxContentSpace)
        .fillMaxHeight()
        .verticalScroll(rememberScrollState())
        .padding(vertical = LocalSpace.current.vertical.padding)
        .imePadding()
        .safeDrawingPadding(),
    ) {
      val title by remember { derivedStateOf { model.title } }
      val description by remember { derivedStateOf { model.description } }
      val body by remember { derivedStateOf { model.body } }
      val tagList by remember { derivedStateOf { model.tagList.joinToString(",") } }
      val errorMsgState = remember { derivedStateOf { model.errorMsg } }

      IconButton(
        onClick = { addArticleComponent.send(AddArticleIntent.BackWithoutPublish) },
        modifier = Modifier.align(Alignment.Start)
          .padding(horizontal = LocalSpace.current.horizontal.padding)
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Default.ArrowBack,
          contentDescription = "Go back"
        )
      }

      OutlinedTextField(
        value = title,
        onValueChange = { addArticleComponent.send(AddArticleIntent.TitleChanged(it)) },
        label = { Text("Title") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
          .padding(horizontal = LocalSpace.current.horizontal.padding),
      )

      OutlinedTextField(
        value = description,
        onValueChange = { addArticleComponent.send(AddArticleIntent.DescriptionChanged(it)) },
        label = { Text("Description") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
          .padding(horizontal = LocalSpace.current.horizontal.padding),
      )


      OutlinedTextField(
        value = body,
        onValueChange = { addArticleComponent.send(AddArticleIntent.BodyChanged(it)) },
        label = { Text("Article Content") },
        minLines = 5,
        modifier = Modifier.fillMaxWidth()
          .padding(horizontal = LocalSpace.current.horizontal.padding),
      )

      OutlinedTextField(
        value = tagList,
        onValueChange = { addArticleComponent.send(AddArticleIntent.TagListChanged(it)) },
        label = { Text("Tags") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
          .padding(horizontal = LocalSpace.current.horizontal.padding),
      )

      Button(
        onClick = { addArticleComponent.send(AddArticleIntent.Publish) },
        modifier = Modifier.padding(horizontal = LocalSpace.current.horizontal.padding),
      ) {
        Text("Publish")
      }

      val showErrorMsg = remember { derivedStateOf { model.errorMsg.isNotBlank() } }
      AnimatedVisibility(
        visible = showErrorMsg.value,
      ) {
        ErrorMessage(errorMsgState)
      }

    }
  }
}

@Composable
private fun ErrorMessage(message: State<String>, modifier: Modifier = Modifier) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
  ) {
    Icon(
      imageVector = Icons.Filled.Warning,
      contentDescription = "Error",
      tint = androidx.compose.ui.graphics.Color.Red
    )
    Spacer(modifier = Modifier.width(LocalSpace.current.horizontal.spacing))
    Text(
      text = message.value,
      color = androidx.compose.ui.graphics.Color.Red
    )
  }
}