package mikufan.cx.conduit.backend.config

import org.koin.dsl.module

/**
 * can optionally take a `args: Array<String>` passed from command line from main method
 */
val configModule = module {
  // in the future, replace it with config library reading from various sources
  single { loadConfig(getOrNull<Array<String>>() ?: emptyArray()) }
}