package com.github.anddd7.want2eat.service

import com.github.anddd7.want2eat.infrastructure.client.MqClient
import com.github.anddd7.want2eat.infrastructure.client.MqMessageDto
import com.github.anddd7.want2eat.infrastructure.client.MqMessageTopic
import com.github.anddd7.want2eat.infrastructure.repository.MerchantAccountRepository
import com.github.anddd7.want2eat.infrastructure.repository.WithdrawRecordEntity
import com.github.anddd7.want2eat.infrastructure.repository.WithdrawRecordRepository
import com.github.anddd7.want2eat.infrastructure.repository.WithdrawStatus
import com.github.anddd7.want2eat.service.viewobject.WithdrawRequest
import org.springframework.stereotype.Service

@Service
class WithdrawService(
    private val merchantAccountRepository: MerchantAccountRepository,
    private val withdrawRecordRepository: WithdrawRecordRepository,
    private val mqClient: MqClient,
) {
    fun request(request: WithdrawRequest) {
        val merchantAccount = merchantAccountRepository.getById(request.merchantAccountId)
        merchantAccountRepository.save(merchantAccount.copy(balance = merchantAccount.balance - request.amount))
        val withdrawRecord = withdrawRecordRepository.save(request.buildRecord())

        mqClient.send(
            MqMessageDto(
                MqMessageTopic.WITHDRAW,
                callback = "/merchant-account/balance/withdraw/${withdrawRecord.id}/confirmation",
                payload = request
            )
        )
    }

    private fun WithdrawRequest.buildRecord() = WithdrawRecordEntity(
        merchantAccountId,
        amount,
        currency,
        channel,
        status = WithdrawStatus.IN_PROGRESS
    )
}
