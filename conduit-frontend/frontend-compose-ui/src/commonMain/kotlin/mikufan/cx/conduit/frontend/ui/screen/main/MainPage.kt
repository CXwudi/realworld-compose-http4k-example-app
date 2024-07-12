package mikufan.cx.conduit.frontend.ui.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MainPage() {
  Column(modifier = Modifier
    .fillMaxSize()
    .background(MaterialTheme.colorScheme.background)
    .windowInsetsPadding(WindowInsets.systemBars)) {
    Text("TODO: main page", color = MaterialTheme.colorScheme.onBackground)
  }
}