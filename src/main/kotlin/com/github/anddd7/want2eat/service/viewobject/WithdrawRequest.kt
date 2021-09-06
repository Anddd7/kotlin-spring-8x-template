package com.github.anddd7.want2eat.service.viewobject

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
