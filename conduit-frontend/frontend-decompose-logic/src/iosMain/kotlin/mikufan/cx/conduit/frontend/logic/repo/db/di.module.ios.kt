package mikufan.cx.conduit.frontend.logic.repo.db

import org.koin.dsl.module

internal actual val dbDriverProvider = module {
  single { provideDbDriver(AppDb.Schema) }
}