package com.github.anddd7.want2eat.service

import com.github.anddd7.want2eat.controller.WithdrawRequest
import com.github.anddd7.want2eat.infrastructure.repository.MerchantAccountRepository
import com.github.anddd7.want2eat.infrastructure.repository.WithdrawRecordEntity
import com.github.anddd7.want2eat.infrastructure.repository.WithdrawRecordRepository
import com.github.anddd7.want2eat.infrastructure.repository.WithdrawStatus
import org.springframework.stereotype.Service

@Service
class WithdrawService(
    private val merchantAccountRepository: MerchantAccountRepository,
    private val withdrawRecordRepository: WithdrawRecordRepository,
) {
    fun request(request: WithdrawRequest) {
        val merchantAccount = merchantAccountRepository.getById(request.merchantAccountId)
        merchantAccountRepository.save(merchantAccount.copy(balance = merchantAccount.balance - request.amount))
        withdrawRecordRepository.save(request.toRecordEntity())
    }

    private fun WithdrawRequest.toRecordEntity() = WithdrawRecordEntity(
        merchantAccountId,
        amount,
        currency,
        channel,
        status = WithdrawStatus.IN_PROGRESS
    )
}
