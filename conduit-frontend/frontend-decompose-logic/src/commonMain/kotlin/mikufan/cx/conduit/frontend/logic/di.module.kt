package mikufan.cx.conduit.frontend.logic

import mikufan.cx.conduit.frontend.logic.component.decomposeViewModelModules
import mikufan.cx.conduit.frontend.logic.repo.repoModules
import mikufan.cx.conduit.frontend.logic.service.serviceModule

/**
 * All modules required for setting up the Koin DI to handle the whole Decompose Navigation tree.
 */
val allModules =
  // repo layer
  repoModules +
  // service layer (business logic)
  listOf(serviceModule) +
  // decompose layer (multiplatform navigation + MVI)
  decomposeViewModelModules
