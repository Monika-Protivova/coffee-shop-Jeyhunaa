package com.motycka.edu

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

fun main(args: Array<String>) {
    // Entry point left empty intentionally
}

object MenuItemTable : LongIdTable("menu_item") {
    val name = text("name")
    val description = text("description")
    val price = double("price")
    val deletedFlag = bool("is_deleted").default(false)
}

object OrderTable : LongIdTable("orders") {
    val customerName = text("customer_name")
    val orderTimestamp = datetime("order_date")
    val totalPrice = double("total_amount")
}

object OrderItemTable : LongIdTable("order_item") {
    val menuItemRef = reference("menu_item_id", MenuItemTable.id)
    val orderRef = reference("order_id", OrderTable.id)
    val quantity = integer("quantity")
}
