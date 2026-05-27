package com.neobank.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "credit_payments")
open class CreditPayment @JvmOverloads constructor(

    @Column(name = "due_date", nullable = false)
    open var dueDate: LocalDate? = null,

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    open var amount: BigDecimal = BigDecimal.ZERO,

    @Column(name = "paid", nullable = false)
    open var paid: Boolean = false,

    @Column(name = "paid_date")
    open var paidDate: LocalDate? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_id", nullable = false)
    open var credit: Credit? = null

) : BaseEntity()