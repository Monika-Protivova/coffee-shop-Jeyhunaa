package com.motycka.edu.customer

import com.motycka.edu.user.UserId

class InternalCustomerService(
    private val customerRepo: CustomerRepository
) {

    fun fetchCustomer(userId: UserId): CustomerDTO? {
        return customerRepo.selectCustomer(userId)
    }

    fun fetchDiscountPercent(userId: UserId): Double {
        return customerRepo.selectCustomer(userId)?.discountPercent
            ?: error("No customer record associated with this user")
    }
}
