package mikufan.cx.conduit.frontend.logic.repo.db

import kotlinx.coroutines.runBlocking
import org.koin.dsl.module

internal actual val dbDriverProvider = module {
  single { runBlocking { provideDbDriver(AppDb.Schema) } }
}