package com.hanghae.commerce.claim.api.dto

import com.hanghae.commerce.payment.domain.BankAccount

data class OrderCancelRequest(
    val orderId: String,
    val userId: String,
    val reason: String,
    val bankAccount: BankAccount,
)
