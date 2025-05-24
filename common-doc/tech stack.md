# Tech Stack

This document describes the tech stack used in this project, so that LLM can understand what libraries to use when generating codes.

When doing tasks, LLM should try to read the documentation to get the latest version of APIs and provide precise answers. To do so, try to use any tools available to you to search, read, crawl the document from the following URLs. Or use context7 for searching documentation

## Common

- [Kotlin](https://kotlinlang.org/) - Modern programming language for the JVM/Android/iOS/Js/Wasm
- [kotlinx.serialization](https://kotlinlang.org/docs/serialization.html) - Library for serializing Kotlin objects
- [Gradle](https://gradle.org/) - Build tool, used for both Frontend and Backend

## Backend

- [Http4k](https://www.http4k.org/) - Functional toolkit for Kotlin HTTP applications
- [Exposed](https://www.jetbrains.com/exposed/) - Kotlin SQL Library by JetBrains
- [PostgreSQL](https://www.postgresql.org/) - Advanced open source database
- [Flyway](https://www.red-gate.com/products/flyway/) - Database migration tool
- [JUnit 5](https://junit.org/junit5/) - Modern testing framework for Java and JVM
- [Mockk](https://mockk.io/) - Mocking library for Kotlin

## Frontend

- [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/) - Declarative UI framework by JetBrains
- [Decompose](https://arkivanov.github.io/Decompose/) - Lifecycle-aware components and navigation for Kotlin Multiplatform
- [MVIKotlin](https://arkivanov.github.io/MVIKotlin/) - MVI pattern implementation for Kotlin Multiplatform
- [Coil](https://coil-kt.github.io/coil/) - Image loading for Android and Compose Multiplatform
- [Coroutines](https://kotlinlang.org/docs/coroutines-guide.html) - Asynchronous programming framework
- [Koin](https://insert-koin.io/) - Pragmatic dependency injection framework
- [Ktor](https://ktor.io/) - Framework for building asynchronous servers and clients
- [Ktorfit](https://github.com/Foso/Ktorfit) - HTTP client generator inspired by Retrofit
- [KStore](https://github.com/xxfast/KStore) - Multiplatform library for saving objects to disk

## Dev

- [VSCode](https://code.visualstudio.com/) - for AI coding (VSCode doesn't properly configured Kotlin support so ignore any Kotlin import-related errors)
- [IntelliJ](https://www.jetbrains.com/idea/) - for backend development
- [Android Studio](https://developer.android.com/studio) - for frontend development
- [Docker](https://www.docker.com/) - for backend services
- [GitHub](https://github.com/) - git and CI
- [draw.io](https://www.drawio.com/) - for diagrams

### AI Tools

- [Cline](https://cline.bot/): Autonomous coding agent right in your IDE
- [Augment Code](https://www.augmentcode.com/): AI-powered coding platform for professional software engineers
