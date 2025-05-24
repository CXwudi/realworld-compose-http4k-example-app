# Common Documentation

This documentation serves both as a guide for new contributors, and LLM's prompt

## Develop

Install Android Studio and IntelliJ IDEA, then:

- Frontend: Open the [`conduit-frontend`](../conduit-frontend) directory in Android Studio.
- Backend: Open the [`conduit-backend`](../conduit-backend) directory in IntelliJ IDEA.

## How it works

The project is divided into 4 modules:

1. [`conduit-common`](../conduit-common) - the shared code between the client and the server.
2. [`conduit-frontend`](../conduit-frontend) - the KMP client source code.
3. [`conduit-backend`](../conduit-backend) - the server source code.
4. [`build-src`](../build-src) - shared Gradle build logic, including the [version catalog](./build-src/libs.versions.toml) that is used globally across the project.
