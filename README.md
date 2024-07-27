# ![RealWorld Example App](logo.png)

<h3>2024.08 Update: I am pausing the implementation for now. More information at <a href="https://github.com/gothinkster/realworld/discussions/1545#discussioncomment-10165234">this discussion</a></h3>

> ### Compose Multiplatform + http4k codebase containing real world examples (CRUD, auth, advanced patterns, etc) that adheres to the [RealWorld](https://github.com/gothinkster/realworld) spec and API.

This codebase was created to demonstrate a fully fledged fullstack application built with **[Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)** and **[http4k](https://www.http4k.org/)** including CRUD operations, authentication, routing, pagination, and more.

We've gone to great lengths to adhere to the community styleguides & best practices of **Compose Multiplatform** and **http4k**.

For more information on how this works with other frontends/backends, head over to the [RealWorld](https://github.com/gothinkster/realworld) repo.

## How it works

The project is divided into 4 modules:

1. [`conduit-common`](./conduit-common) - the shared code between the client and the server.
2. [`conduit-frontend`](./conduit-frontend) - the KMP client source code.
3. [`conduit-backend`](./conduit-backend) - the server source code.
4. [`build-src`](./build-src) - shared Gradle build logic, including the [version catalog](./build-src/libs.versions.toml) that is used globally across the project.

## Develop

Install Android Studio and IntelliJ IDEA, then:

- Frontend: Open the `conduit-frontend` directory in Android Studio.
- Backend: Open the `conduit-backend` directory in IntelliJ IDEA.

### About Frontend

For frontend development, you need to follow this [guide](https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-setup.html#check-your-environment) to set up the Compose Multiplatform development environment.

`conduit-frontend` declares all 4 platforms of JVM, Android, JS, and iOS. However, iOS is only declared in Gradle but not developed since I don't have a Mac machine.

`conduit-frontend` contains multiplatform tests that run on all 4 platforms. However, tests on the JS platform require a browser, so far `useChromiumHeadless()` is defined in [`kmp-library.gradle.kts`](build-src/plugins/multiplatform-library/src/main/kotlin/my/kmp-library.gradle.kts), which means you need to install Chromium for running tests on the JS platform. If you already have a Chrome browser, feel free to change to `useChromeHeadless()`(or `useFirefox()` and others) for your convenience.
