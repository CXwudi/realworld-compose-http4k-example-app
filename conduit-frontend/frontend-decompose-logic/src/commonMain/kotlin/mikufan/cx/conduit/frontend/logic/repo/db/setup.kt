package mikufan.cx.conduit.frontend.logic.repo.db

import app.cash.sqldelight.db.SqlDriver

fun getAppDb(sqlDriver: SqlDriver) = AppDb(sqlDriver)