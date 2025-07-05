package com.motycka.edu

import com.motycka.edu.customer.CustomerTable
import com.motycka.edu.menu.MenuItemTable
import com.motycka.edu.order.OrderTable
import com.motycka.edu.user.UserTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

// Fingers crossed this is heading in the right direction

fun setupTestDatabase(vararg tables: Table) {
    Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;",
        driver = "org.h2.Driver"
    )

    transaction {
        SchemaUtils.create(*tables)
    }
}

suspend fun <T> runTestTransaction(block: suspend () -> T): T {
    return newSuspendedTransaction(Dispatchers.IO) {
        block()
    }
}
