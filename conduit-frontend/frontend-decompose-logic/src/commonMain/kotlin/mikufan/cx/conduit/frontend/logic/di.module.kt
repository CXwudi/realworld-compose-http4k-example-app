package mikufan.cx.conduit.frontend.logic

import mikufan.cx.conduit.frontend.logic.component.componentFactoryModule
import mikufan.cx.conduit.frontend.logic.component.storeModule
import mikufan.cx.conduit.frontend.logic.repo.repoModule
import mikufan.cx.conduit.frontend.logic.service.serviceModule

/**
 * All modules required for setting up the Koin DI to handle the whole Decompose Navigation tree.
 */
val allModules = listOf(repoModule, serviceModule, storeModule, componentFactoryModule)