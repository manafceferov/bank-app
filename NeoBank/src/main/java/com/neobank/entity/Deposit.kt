package com.neobank.entity

import com.neobank.enums.DepositStatus
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "deposits")
open class Deposit @JvmOverloads constructor(

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    open var amount: BigDecimal = BigDecimal.ZERO,

    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    open var interestRate: BigDecimal = BigDecimal.ZERO,

    @Column(name = "duration_months", nullable = false)
    open var durationMonths: Int = 12,

    @Column(name = "start_date", nullable = false)
    open var startDate: LocalDate? = null,

    @Column(name = "end_date", nullable = false)
    open var endDate: LocalDate? = null,

    @Column(name = "expected_profit", precision = 19, scale = 2)
    open var expectedProfit: BigDecimal = BigDecimal.ZERO,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    open var status: DepositStatus = DepositStatus.ACTIVE,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    open var account: Account? = null

) : BaseEntity()