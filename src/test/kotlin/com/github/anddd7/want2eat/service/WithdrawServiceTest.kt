package com.github.anddd7.want2eat.service

import com.github.anddd7.want2eat.infrastructure.client.MqClient
import com.github.anddd7.want2eat.infrastructure.client.MqMessageDto
import com.github.anddd7.want2eat.infrastructure.client.MqMessageTopic
import com.github.anddd7.want2eat.infrastructure.repository.MerchantAccountEntity
import com.github.anddd7.want2eat.infrastructure.repository.MerchantAccountRepository
import com.github.anddd7.want2eat.infrastructure.repository.WithdrawRecordEntity
import com.github.anddd7.want2eat.infrastructure.repository.WithdrawRecordRepository
import com.github.anddd7.want2eat.infrastructure.repository.WithdrawStatus
import com.github.anddd7.want2eat.service.viewobject.Currency
import com.github.anddd7.want2eat.service.viewobject.InsufficientBalanceException
import com.github.anddd7.want2eat.service.viewobject.PaymentMethod
import com.github.anddd7.want2eat.service.viewobject.WithdrawRequest
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class WithdrawServiceTest {
    private val merchantAccountRepository = mockk<MerchantAccountRepository>()
    private val withdrawRecordRepository = mockk<WithdrawRecordRepository>()
    private val mqClient = mockk<MqClient>()
    private val withdrawService = WithdrawService(merchantAccountRepository, withdrawRecordRepository, mqClient)

    private val merchantAccount = MerchantAccountEntity(
        id = 10001L,
        balance = 100,
    )

    @AfterEach
    internal fun tearDown() {
        clearAllMocks()
    }

    private val tempRequest = WithdrawRequest(
        merchantAccountId = merchantAccount.id,
        amount = 100,
        currency = Currency.CHN_YUAN,
        channel = PaymentMethod.WECHATPAY,
    )

    @Test
    fun `should successful withdraw the amount, update balance and create withdraw record`() {
        val request = tempRequest.copy(amount = 100)

        every { merchantAccountRepository.getById(any()) } returns merchantAccount
        every { merchantAccountRepository.deductBalance(any(), any()) } returns 1
        every { withdrawRecordRepository.save(any()) } returnsArgument 0
        every { mqClient.send(any()) } just runs

        withdrawService.request(request)

        verify {
            merchantAccountRepository.getById(merchantAccount.id)
            merchantAccountRepository.deductBalance(merchantAccount.id, request.amount)
            withdrawRecordRepository.save(
                WithdrawRecordEntity(
                    merchantAccountId = request.merchantAccountId,
                    amount = request.amount,
                    currency = request.currency,
                    channel = request.channel,
                    status = WithdrawStatus.IN_PROGRESS
                )
            )
            mqClient.send(
                MqMessageDto(
                    topic = MqMessageTopic.WITHDRAW,
                    callback = "/merchant-account/balance/withdraw/0/confirmation",
                    payload = request
                )
            )
        }
    }

    @Test
    fun `should successful withdraw when deduct amount is less than the balance`() {
        val request = tempRequest.copy(amount = 99)

        every { merchantAccountRepository.getById(any()) } returns merchantAccount
        every { merchantAccountRepository.deductBalance(any(), any()) } returns 1
        every { withdrawRecordRepository.save(any()) } returnsArgument 0
        every { mqClient.send(any()) } just runs

        withdrawService.request(request)

        verify {
            merchantAccountRepository.getById(merchantAccount.id)
            merchantAccountRepository.deductBalance(merchantAccount.id, request.amount)
            withdrawRecordRepository.save(
                WithdrawRecordEntity(
                    merchantAccountId = request.merchantAccountId,
                    amount = request.amount,
                    currency = request.currency,
                    channel = request.channel,
                    status = WithdrawStatus.IN_PROGRESS
                )
            )
            mqClient.send(
                MqMessageDto(
                    topic = MqMessageTopic.WITHDRAW,
                    callback = "/merchant-account/balance/withdraw/0/confirmation",
                    payload = request
                )
            )
        }
    }

    @Test
    fun `should throw exception when deduct amount is more than the balance`() {
        val request = tempRequest.copy(amount = 101)

        every { merchantAccountRepository.getById(any()) } returns merchantAccount

        assertThrows<InsufficientBalanceException> {
            withdrawService.request(request)
        }

        verify {
            merchantAccountRepository.getById(merchantAccount.id)
        }

        verify(inverse = true) {
            merchantAccountRepository.deductBalance(any(), any())
            withdrawRecordRepository.save(any())
            mqClient.send(any())
        }
    }

    @Test
    fun `should throw exception when deduct amount failed due to db conflict`() {
        val request = tempRequest.copy(amount = 100)

        every { merchantAccountRepository.getById(any()) } returns merchantAccount
        every { merchantAccountRepository.deductBalance(any(), any()) } returns 0

        assertThrows<InsufficientBalanceException> {
            withdrawService.request(request)
        }

        verify {
            merchantAccountRepository.getById(merchantAccount.id)
            merchantAccountRepository.deductBalance(merchantAccount.id, request.amount)
        }

        verify(inverse = true) {
            withdrawRecordRepository.save(any())
            mqClient.send(any())
        }
    }
}
