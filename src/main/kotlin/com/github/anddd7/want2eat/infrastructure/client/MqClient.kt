package com.github.anddd7.want2eat.infrastructure.client

import org.springframework.stereotype.Component

@Component
interface MqClient {
    fun send(mqMessageDto: MqMessageDto)
}

data class MqMessageDto(
    val topic: MqMessageTopic,
    val callback: String,
    val payload: Any,
)

enum class MqMessageTopic(private val value: String) {
    WITHDRAW("merchant_account_balance_withdraw");

    override fun toString() = value
}
