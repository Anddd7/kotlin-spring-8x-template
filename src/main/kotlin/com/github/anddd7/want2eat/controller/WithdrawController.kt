package com.github.anddd7.want2eat.controller

import com.github.anddd7.want2eat.service.WithdrawService
import com.github.anddd7.want2eat.service.viewobject.ErrorResponse
import com.github.anddd7.want2eat.service.viewobject.InsufficientBalanceException
import com.github.anddd7.want2eat.service.viewobject.WithdrawRequest
import org.springframework.http.ResponseEntity
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
    fun withdraw(@RequestBody body: WithdrawRequest): ResponseEntity<ErrorResponse> {
        return try {
            withdrawService.request(body)

            ResponseEntity.ok().build()
        } catch (e: InsufficientBalanceException) {
            ResponseEntity.badRequest().body(ErrorResponse(e.message))
        }
    }
}
