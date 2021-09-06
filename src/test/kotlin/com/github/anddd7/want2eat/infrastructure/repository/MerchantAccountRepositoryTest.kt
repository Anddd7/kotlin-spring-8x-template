package com.github.anddd7.want2eat.infrastructure.repository

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class MerchantAccountRepositoryTest {
    @Autowired
    private lateinit var merchantAccountRepository: MerchantAccountRepository

    private val merchantAccount = MerchantAccountEntity(10001L, 100)

    @Test
    fun `should return the saved entity when getById`() {
        merchantAccountRepository.save(merchantAccount)
        val result = merchantAccountRepository.getById(merchantAccount.id)

        Assertions.assertThat(result).isEqualTo(merchantAccount)
    }

    @Test
    fun `should deduct balance of the merchant account successfully when the deduct amount is equals balance`() {
        merchantAccountRepository.save(merchantAccount)
        val updated = merchantAccountRepository.deductBalance(merchantAccount.id, 100)

        val result = merchantAccountRepository.getById(merchantAccount.id)

        Assertions.assertThat(updated).isEqualTo(1)
        Assertions.assertThat(result.balance).isEqualTo(0)
    }

    @Test
    fun `should not deduct balance when the deduct amount is more than balance`() {
        merchantAccountRepository.save(merchantAccount)
        val updated = merchantAccountRepository.deductBalance(merchantAccount.id, 101)
        val result = merchantAccountRepository.getById(merchantAccount.id)

        Assertions.assertThat(updated).isEqualTo(0)
        Assertions.assertThat(result.balance).isEqualTo(100)
    }
}
