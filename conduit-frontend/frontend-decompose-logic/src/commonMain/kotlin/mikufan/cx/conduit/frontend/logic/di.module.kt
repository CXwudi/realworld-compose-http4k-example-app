package mikufan.cx.conduit.frontend.logic

import mikufan.cx.conduit.frontend.logic.component.storeModule
import mikufan.cx.conduit.frontend.logic.repo.db.dbModule
import mikufan.cx.conduit.frontend.logic.repo.kstore.kstoreModule
import mikufan.cx.conduit.frontend.logic.service.serviceModule

val allModules = listOf(dbModule, kstoreModule, serviceModule, storeModule, )