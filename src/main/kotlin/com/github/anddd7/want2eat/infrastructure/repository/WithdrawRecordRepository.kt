package com.github.anddd7.want2eat.infrastructure.repository

import com.github.anddd7.want2eat.service.viewobject.Currency
import com.github.anddd7.want2eat.service.viewobject.PaymentMethod
import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

interface WithdrawRecordRepository : JpaRepository<WithdrawRecordEntity, Long>

@Entity
@Table(name = "withdraw_record")
data class WithdrawRecordEntity(
    @Column(name = "merchantAccountId")
    val merchantAccountId: Long,

    @Column(name = "amount")
    val amount: Int,

    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    val currency: Currency,

    @Column(name = "channel")
    @Enumerated(EnumType.STRING)
    val channel: PaymentMethod,

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    val status: WithdrawStatus,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
)

enum class WithdrawStatus {
    IN_PROGRESS, COMPLETED
}
