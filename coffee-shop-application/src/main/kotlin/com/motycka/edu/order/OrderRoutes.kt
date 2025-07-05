package com.motycka.edu.order

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private val logger = KotlinLogging.logger {}

private const val ERROR_ORDER_NOT_FOUND = "Order not found"
private const val ERROR_INVALID_ID = "Invalid order ID format"

fun Route.orderRoutes(
    orderService: OrderService,
    basePath: String
) {
    route("$basePath/orders") {

        post {
            val orderRequest = call.receive<OrderRequest>()
            val createdOrder = orderService.createOrder(orderRequest)
            call.respond(HttpStatusCode.Created, createdOrder)
        }

        get {
            val allOrders = orderService.findAllOrders()
            call.respond(HttpStatusCode.OK, allOrders)
        }

        get("{id}") {
            val idParam = call.parameters["id"]?.toLongOrNull()
            if (idParam == null) {
                call.respond(HttpStatusCode.BadRequest, ERROR_INVALID_ID)
                return@get
            }

            val order = orderService.findOrderById(OrderId(idParam))
            if (order != null) {
                call.respond(HttpStatusCode.OK, order)
            } else {
                call.respond(HttpStatusCode.NotFound, ERROR_ORDER_NOT_FOUND)
            }
        }

        put("{id}") {
            val idParam = call.parameters["id"]?.toLongOrNull()
            if (idParam == null) {
                call.respond(HttpStatusCode.BadRequest, ERROR_INVALID_ID)
                return@put
            }

            val updateRequest = call.receive<OrderUpdateRequest>()
            val result = orderService.updateOrderStatus(OrderId(idParam), updateRequest)
            if (result != null) {
                call.respond(HttpStatusCode.OK, result)
            } else {
                call.respond(HttpStatusCode.NotFound, ERROR_ORDER_NOT_FOUND)
            }
        }
    }
}
