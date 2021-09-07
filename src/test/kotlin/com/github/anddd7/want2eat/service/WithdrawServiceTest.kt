package com.github.anddd7.want2eat.service

import com.github.anddd7.want2eat.controller.Currency
import com.github.anddd7.want2eat.controller.PaymentMethod
import com.github.anddd7.want2eat.controller.WithdrawRequest
import com.github.anddd7.want2eat.infrastructure.repository.MerchantAccountEntity
import com.github.anddd7.want2eat.infrastructure.repository.MerchantAccountRepository
import com.github.anddd7.want2eat.infrastructure.repository.WithdrawRecordEntity
import com.github.anddd7.want2eat.infrastructure.repository.WithdrawRecordRepository
import com.github.anddd7.want2eat.infrastructure.repository.WithdrawStatus
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

internal class WithdrawServiceTest {
    private val merchantAccountRepository = mockk<MerchantAccountRepository>()
    private val withdrawRecordRepository = mockk<WithdrawRecordRepository>()
    private val withdrawService = WithdrawService(merchantAccountRepository, withdrawRecordRepository)

    private val merchantAccount = MerchantAccountEntity(
        id = 10001L,
        balance = 100,
    )

    @AfterEach
    internal fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should successful withdraw the amount, update balance and create withdraw record`() {
        val request = WithdrawRequest(
            merchantAccountId = merchantAccount.id,
            amount = 100,
            currency = Currency.CHN_YUAN,
            channel = PaymentMethod.WECHATPAY,
        )

        every { merchantAccountRepository.getById(any()) } returns merchantAccount
        every { merchantAccountRepository.save(any()) } returnsArgument 0
        every { withdrawRecordRepository.save(any()) } returnsArgument 0

        withdrawService.request(request)

        verify {
            merchantAccountRepository.getById(merchantAccount.id)
            merchantAccountRepository.save(merchantAccount.copy(balance = merchantAccount.balance - request.amount))
            withdrawRecordRepository.save(
                WithdrawRecordEntity(
                    merchantAccountId = request.merchantAccountId,
                    amount = request.amount,
                    currency = request.currency,
                    channel = request.channel,
                    status = WithdrawStatus.IN_PROGRESS
                )
            )
        }
    }
}
