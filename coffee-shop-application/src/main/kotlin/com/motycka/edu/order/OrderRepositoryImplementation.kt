package com.motycka.edu.order

import org.jetbrains.exposed.sql.transactions.transaction

class OrderRepositoryImpl : OrderRepository {

    override fun selectAll(): List<OrderDTO> = transaction {
        OrderDAO.all().map { dao -> dao.toDTO() }
    }

    override fun selectById(id: OrderId): OrderDTO? = transaction {
        OrderDAO.findById(id.value)?.toDTO()
    }

    override fun create(order: OrderDTO): OrderDTO = transaction {
        OrderDAO.new {
            customerId = order.customerId
            status = order.status
        }.toDTO()
    }

    override fun update(order: OrderDTO): OrderDTO = transaction {
        val idValue = order.id?.value
            ?: throw IllegalArgumentException("Missing ID: Cannot update order without an ID")

        val dao = OrderDAO.findById(idValue)
            ?: throw NoSuchElementException("Order with ID $idValue does not exist")

        dao.apply {
            status = order.status
        }.toDTO()
    }
}
