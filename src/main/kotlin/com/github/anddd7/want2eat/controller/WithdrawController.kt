package com.github.anddd7.want2eat.controller

import com.github.anddd7.want2eat.service.WithdrawService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/merchant-account/balance/withdraw")
class WithdrawController(
    private val withdrawService: WithdrawService,
) {
    @PostMapping
    fun withdraw(@RequestBody body: WithdrawRequest) {
        withdrawService.request(body)
    }
}

data class WithdrawRequest(
    val merchantAccountId: Long,
    val amount: Int,
    val currency: Currency,
    val channel: PaymentMethod
)

enum class Currency {
    CHN_YUAN
}

enum class PaymentMethod {
    WECHATPAY, ALIPAY, UNIONPAY, APPLEPAY
}
