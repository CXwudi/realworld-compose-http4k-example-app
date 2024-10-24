package mikufan.cx.conduit.frontend.logic.repo.api

import io.ktor.client.engine.HttpClientEngineFactory


actual val Js: HttpClientEngineFactory<*> = io.ktor.client.engine.js.Js