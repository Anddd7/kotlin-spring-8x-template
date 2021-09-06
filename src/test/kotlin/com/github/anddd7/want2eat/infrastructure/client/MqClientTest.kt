package com.github.anddd7.want2eat.infrastructure.client

import com.github.anddd7.config.RestTemplateConfig
import com.github.anddd7.want2eat.service.viewobject.Currency
import com.github.anddd7.want2eat.service.viewobject.PaymentMethod
import com.github.anddd7.want2eat.service.viewobject.WithdrawRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.http.HttpMethod
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.web.client.RestTemplate

@RestClientTest(
    components = [MqClient::class, RestTemplateConfig::class],
    properties = ["mq_base_url=http://mq.internal"]
)
internal class MqClientTest {
    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Autowired
    private lateinit var client: MqClient
    private val server: MockRestServiceServer by lazy {
        MockRestServiceServer.createServer(restTemplate)
    }

    @Test
    fun `should send withdraw message via mq client`() {
        val message = MqMessageDto(
            topic = MqMessageTopic.WITHDRAW,
            callback = "/merchant-account/balance/withdraw/0/confirmation",
            payload = WithdrawRequest(
                merchantAccountId = 10001L,
                amount = 100,
                currency = Currency.CHN_YUAN,
                channel = PaymentMethod.WECHATPAY,
            )
        )

        server
            .expect(MockRestRequestMatchers.requestTo("http://mq.internal/messages"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andExpect(
                MockRestRequestMatchers.content().json(
                    """
                    {"topic":"WITHDRAW","callback":"/merchant-account/balance/withdraw/0/confirmation","payload":{"merchantAccountId":10001,"amount":100,"currency":"CHN_YUAN","channel":"WECHATPAY"}}
                    """.trimIndent()
                )
            )
            .andRespond(MockRestResponseCreators.withSuccess())

        client.send(message)

        server.verify()
    }
}
