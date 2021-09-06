package com.github.anddd7.want2eat

import com.github.anddd7.TestApplication
import com.github.anddd7.want2eat.infrastructure.repository.MerchantAccountEntity
import com.github.anddd7.want2eat.infrastructure.repository.MerchantAccountRepository
import com.github.anddd7.want2eat.infrastructure.repository.WithdrawRecordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.client.ExpectedCount.manyTimes
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.web.client.RestTemplate

@ExtendWith(SpringExtension::class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [TestApplication::class],
    properties = ["mq_base_url=http://mq.internal"]
)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@AutoConfigureMockRestServiceServer
@ActiveProfiles("test")
internal class WithdrawControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var merchantAccountRepository: MerchantAccountRepository

    @Autowired
    private lateinit var withdrawRecordRepository: WithdrawRecordRepository

    @Autowired
    private lateinit var restTemplate: RestTemplate

    private val server: MockRestServiceServer by lazy {
        MockRestServiceServer.createServer(restTemplate)
    }

    private val url = "/merchant-account/balance/withdraw"

    private val merchantAccount = MerchantAccountEntity(10001L, 100)

    @Test
    fun `should execute only once withdraw when concurrent requests`() {
        // set db
        merchantAccountRepository.save(merchantAccount)
        // set api
        server
            .expect(manyTimes(), requestTo("http://mq.internal/messages"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess())

        // concurrency call
        val deductAmount = 100

        runBlocking(Dispatchers.Default) {
            (1..100).map {
                async {
                    mockMvc.post(url) {
                        contentType = MediaType.APPLICATION_JSON
                        content = """
                        {
                           "merchantAccountId": ${merchantAccount.id},
                           "amount": $deductAmount,
                           "currency": "CHN_YUAN",
                           "channel": "WECHATPAY"
                        }
                        """.trimIndent()
                    }
                }
            }.awaitAll()
        }

        // verify
        assertThat(merchantAccountRepository.getById(merchantAccount.id).balance).isEqualTo(0)
        assertThat(withdrawRecordRepository.findAll()).hasSize(1)
    }
}
