package mikufan.cx.conduit.frontend.ui.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.Child
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import mikufan.cx.conduit.frontend.logic.component.main.MainNavComponent
import mikufan.cx.conduit.frontend.logic.component.main.MainNavComponentChild
import mikufan.cx.conduit.frontend.logic.component.main.MainNavIntent
import mikufan.cx.conduit.frontend.logic.component.main.MainNavMode
import mikufan.cx.conduit.frontend.logic.component.main.MainNavState
import mikufan.cx.conduit.frontend.ui.util.SetupPreviewUI

@Composable
@Preview
fun MainPagePreview() {
  SetupPreviewUI {
    val fakeComponent = object : MainNavComponent {
      override val childStack: Value<ChildStack<*, MainNavComponentChild>> = MutableValue(ChildStack(Child.Created(Unit, MainNavComponentChild.MainFeed)))
      override val state: StateFlow<MainNavState> = MutableStateFlow(MainNavState(MainNavMode.NOT_LOGGED_IN, 0))
      override fun send(intent: MainNavIntent) = Unit
    }
    MainNavPage(component = fakeComponent)
  }
}

@Composable
@Preview
fun MainPagePreviewForLoginUser() {
  SetupPreviewUI {
    val fakeComponent = object : MainNavComponent {
      override val childStack: Value<ChildStack<*, MainNavComponentChild>> = MutableValue(ChildStack(Child.Created(Unit, MainNavComponentChild.MainFeed)))
      override val state: StateFlow<MainNavState> = MutableStateFlow(MainNavState(MainNavMode.LOGGED_IN, 0))
      override fun send(intent: MainNavIntent) = Unit
    }
    MainNavPage(component = fakeComponent)
  }
}