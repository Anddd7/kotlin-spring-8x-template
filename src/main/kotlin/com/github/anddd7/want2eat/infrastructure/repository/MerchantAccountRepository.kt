package com.github.anddd7.want2eat.infrastructure.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Repository
interface MerchantAccountRepository : JpaRepository<MerchantAccountEntity, Long> {
    @Modifying(clearAutomatically = true)
    @Query("UPDATE MerchantAccountEntity set balance = balance - :deductAmount where id = :id and balance >= :deductAmount")
    fun deductBalance(id: Long, deductAmount: Int): Int
}

@Entity
@Table(name = "merchant_account")
data class MerchantAccountEntity(
    @Id
    val id: Long,
    @Column(name = "balance")
    val balance: Int,
)
