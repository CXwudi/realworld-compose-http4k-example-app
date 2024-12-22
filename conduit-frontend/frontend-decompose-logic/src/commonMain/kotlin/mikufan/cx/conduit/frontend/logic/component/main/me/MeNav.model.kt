package mikufan.cx.conduit.frontend.logic.component.main.me

sealed interface MeNavComponentChild {
  data class MePage(val mePageComponent: MePageComponent) : MeNavComponentChild
}

