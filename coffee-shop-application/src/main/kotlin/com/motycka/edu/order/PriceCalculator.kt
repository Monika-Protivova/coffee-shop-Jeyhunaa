package com.motycka.edu.order

import com.motycka.edu.menu.MenuItemDTO
import java.math.BigDecimal
import java.math.RoundingMode

object PriceCalculator {

    fun calculatePrice(
        menuItems: List<MenuItemDTO>,
        discountPercent: Double,
        orderItems: List<OrderItemDTO> = emptyList()
    ): Double {
        val itemPrices = menuItems.associateBy { it.id }

        val totalBeforeDiscount = orderItems.sumOf { item ->
            val unitPrice = itemPrices[item.menuItemId]?.price ?: 0.0
            unitPrice * item.quantity
        }

        val clampedDiscount = discountPercent.coerceIn(0.0, 100.0)
        val multiplier = 1.0 - (clampedDiscount / 100.0)

        val discountedTotal = totalBeforeDiscount * multiplier

        return BigDecimal(discountedTotal)
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()
    }
}
