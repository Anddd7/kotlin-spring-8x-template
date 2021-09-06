package com.github.anddd7.want2eat.infrastructure.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class MqClient(
    @Value("\${mq_base_url}")
    private val mqBaseUrl: String,
    private val restTemplate: RestTemplate,
) {

    fun send(mqMessageDto: MqMessageDto) {
        restTemplate.postForLocation("$mqBaseUrl/messages", mqMessageDto)
    }
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
