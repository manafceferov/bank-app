package com.neobank.entity

import com.neobank.enums.TransactionType
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "transactions")
open class Transaction @JvmOverloads constructor(

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    open var amount: BigDecimal = BigDecimal.ZERO,

    @Column(name = "currency", nullable = false, length = 3)
    open var currency: String = "AZN",

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    open var transactionType: TransactionType = TransactionType.TRANSFER,

    @Column(name = "description")
    open var description: String? = null,

    @Column(name = "reference_number", unique = true)
    open var referenceNumber: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id")
    open var fromAccount: Account? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id")
    open var toAccount: Account? = null

) : BaseEntity()