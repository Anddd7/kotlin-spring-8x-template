package com.github.anddd7.want2eat.controller

import com.github.anddd7.want2eat.service.WithdrawService
import com.github.anddd7.want2eat.service.viewobject.Currency
import com.github.anddd7.want2eat.service.viewobject.InsufficientBalanceException
import com.github.anddd7.want2eat.service.viewobject.PaymentMethod
import com.github.anddd7.want2eat.service.viewobject.WithdrawRequest
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(controllers = [WithdrawController::class])
internal class WithdrawControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var service: WithdrawService

    private val url = "/merchant-account/balance/withdraw"

    @Test
    fun `should return success when the merchant is able to withdraw`() {
        val request = WithdrawRequest(
            merchantAccountId = 10001L,
            amount = 100,
            currency = Currency.CHN_YUAN,
            channel = PaymentMethod.WECHATPAY,
        )

        every { service.request(request) } just Runs

        mockMvc.post(url) {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                   "merchantAccountId": ${request.merchantAccountId},
                   "amount": ${request.amount},
                   "currency": "${request.currency}",
                   "channel": "${request.channel}"
                }
            """.trimIndent()
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `should return 400 when the merchant's balance is insufficient`() {
        val request = WithdrawRequest(
            merchantAccountId = 10001L,
            amount = 101,
            currency = Currency.CHN_YUAN,
            channel = PaymentMethod.WECHATPAY,
        )

        every { service.request(request) } throws InsufficientBalanceException()

        mockMvc.post(url) {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                   "merchantAccountId": ${request.merchantAccountId},
                   "amount": ${request.amount},
                   "currency": "${request.currency}",
                   "channel": "${request.channel}"
                }
            """.trimIndent()
        }.andExpect {
            status { isBadRequest() }
            content {
                json("""{"message": "balance insufficient"}""")
            }
        }
    }
}
