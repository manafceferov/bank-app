package com.neobank.entity

import com.neobank.enums.CreditStatus
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "credits")
open class Credit @JvmOverloads constructor(

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    open var amount: BigDecimal = BigDecimal.ZERO,

    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    open var interestRate: BigDecimal = BigDecimal.ZERO,

    @Column(name = "duration_months", nullable = false)
    open var durationMonths: Int = 12,

    @Column(name = "monthly_payment", precision = 19, scale = 2)
    open var monthlyPayment: BigDecimal = BigDecimal.ZERO,

    @Column(name = "start_date")
    open var startDate: LocalDate? = null,

    @Column(name = "end_date")
    open var endDate: LocalDate? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    open var status: CreditStatus = CreditStatus.PENDING,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    open var user: User? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    open var account: Account? = null,

    @OneToMany(mappedBy = "credit", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    open var paymentSchedule: MutableList<CreditPayment> = mutableListOf()

) : BaseEntity()