package com.github.anddd7.want2eat.service

import com.github.anddd7.want2eat.controller.WithdrawRequest
import org.springframework.stereotype.Service

@Service
interface WithdrawService {
    fun request(request: WithdrawRequest)
}
