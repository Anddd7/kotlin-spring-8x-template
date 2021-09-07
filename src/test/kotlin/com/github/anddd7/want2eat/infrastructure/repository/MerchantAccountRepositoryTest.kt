package com.github.anddd7.want2eat.infrastructure.repository

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class MerchantAccountRepositoryTest {
    @Autowired
    private lateinit var merchantAccountRepository: MerchantAccountRepository

    @Test
    fun `should return the saved entity when getById`() {
        val merchantAccount = MerchantAccountEntity(10001L, 100)

        merchantAccountRepository.save(merchantAccount)
        val result = merchantAccountRepository.getById(10001L)

        Assertions.assertThat(result).isEqualTo(merchantAccount)
    }
}
