package com.motycka.edu.config

import com.motycka.edu.customer.CustomerDAO
import com.motycka.edu.customer.CustomerTable
import com.motycka.edu.menu.MenuItemDAO
import com.motycka.edu.menu.MenuItemTable
import com.motycka.edu.order.OrderItemTable
import com.motycka.edu.order.OrderTable
import com.motycka.edu.user.UserDAO
import com.motycka.edu.user.UserRole
import com.motycka.edu.user.UserTable
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.initDatabase() {
    Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        driver = "org.h2.Driver",
        user = "root",
        password = ""
    )

    transaction {
        SchemaUtils.create(UserTable, MenuItemTable, CustomerTable, OrderTable, OrderItemTable)

        UserDAO.new {
            username = "admin"
            password = "password" // Passwords should be hashed in real systems
            role = UserRole.STAFF
        }

        UserDAO.new {
            username = "staff"
            password = "password"
            role = UserRole.STAFF
        }

        UserDAO.new {
            username = "customer"
            password = "password"
            role = UserRole.CUSTOMER
        }

        CustomerDAO.new {
            userId = UserDAO.find { UserTable.username eq "admin" }.firstOrNull()?.id?.value
                ?: error("Admin account missing")
            name = "Administrator"
            discountPercent = 20.0
        }

        CustomerDAO.new {
            userId = UserDAO.find { UserTable.username eq "staff" }.firstOrNull()?.id?.value
                ?: error("Staff account missing")
            name = "Staff"
            discountPercent = 15.0
        }

        CustomerDAO.new {
            userId = UserDAO.find { UserTable.username eq "customer" }.firstOrNull()?.id?.value
                ?: error("Customer account missing")
            name = "Coffee Lover"
            discountPercent = 10.0
        }

        MenuItemDAO.new {
            name = "Espresso"
            description = "Rich coffee brewed by forcing hot water through fine grounds."
            price = 2.50
            isDeleted = false
        }

        MenuItemDAO.new {
            name = "Cappuccino"
            description = "Espresso with steamed milk and a fluffy foam layer."
            price = 3.00
            isDeleted = false
        }

        MenuItemDAO.new {
            name = "Latte"
            description = "Smooth espresso blended with steamed milk and light foam."
            price = 3.50
            isDeleted = false
        }

        MenuItemDAO.new {
            name = "Americano"
            description = "Diluted espresso made by adding hot water."
            price = 2.00
            isDeleted = false
        }

        MenuItemDAO.new {
            name = "Mocha"
            description = "Espresso mixed with steamed milk and chocolate syrup."
            price = 3.75
            isDeleted = false
        }
    }
}

suspend fun <T> runSuspendedTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)
