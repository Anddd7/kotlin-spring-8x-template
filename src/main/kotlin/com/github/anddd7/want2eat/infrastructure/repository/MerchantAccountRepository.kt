package com.github.anddd7.want2eat.infrastructure.repository

import org.springframework.stereotype.Repository

@Repository
interface MerchantAccountRepository {
    fun getById(id: Long): MerchantAccountEntity
    fun save(entity: MerchantAccountEntity)
}

data class MerchantAccountEntity(
    val id: Long,
    val balance: Int,
)
