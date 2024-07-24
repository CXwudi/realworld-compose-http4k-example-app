package mikufan.cx.conduit.frontend.logic.component.main

import com.arkivanov.mvikotlin.core.store.StoreFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import mikufan.cx.conduit.frontend.logic.service.UserConfigService



class MainNavStoreFactory(
  private val storeFactory: StoreFactory,
  private val userConfigService: UserConfigService,
  dispatcher: CoroutineDispatcher = Dispatchers.Main
) {

  // should we use state -> navigation or navigation -> state
}