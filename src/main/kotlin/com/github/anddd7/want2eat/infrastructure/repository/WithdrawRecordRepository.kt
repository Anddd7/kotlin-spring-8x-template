package com.github.anddd7.want2eat.infrastructure.repository

import com.github.anddd7.want2eat.controller.Currency
import com.github.anddd7.want2eat.controller.PaymentMethod
import org.springframework.stereotype.Repository

@Repository
interface WithdrawRecordRepository {
    fun save(entity: WithdrawRecordEntity)
}

data class WithdrawRecordEntity(
    val merchantAccountId: Long,
    val amount: Int,
    val currency: Currency,
    val channel: PaymentMethod,
    val status: WithdrawStatus,
    val id: Long = 0,
)

enum class WithdrawStatus {
    IN_PROGRESS, COMPLETED
}
