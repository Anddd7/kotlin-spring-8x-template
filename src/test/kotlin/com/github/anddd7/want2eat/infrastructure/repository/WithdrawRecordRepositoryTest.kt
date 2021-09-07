package com.github.anddd7.want2eat.infrastructure.repository

import com.github.anddd7.want2eat.controller.Currency
import com.github.anddd7.want2eat.controller.PaymentMethod
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class WithdrawRecordRepositoryTest {
    @Autowired
    private lateinit var withdrawRecordRepository: WithdrawRecordRepository

    @Test
    fun `should return the saved entity when getById`() {
        val withdrawRecord = WithdrawRecordEntity(
            merchantAccountId = 10001L,
            amount = 100,
            currency = Currency.CHN_YUAN,
            channel = PaymentMethod.WECHATPAY,
            status = WithdrawStatus.IN_PROGRESS
        )

        val saved = withdrawRecordRepository.save(withdrawRecord)
        val result = withdrawRecordRepository.getById(saved.id)

        Assertions.assertThat(result.id).isNotEqualTo(0)
        Assertions.assertThat(saved).isEqualTo(result)
    }
}
