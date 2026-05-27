package com.neobank.entity

import com.neobank.enums.AccountStatus
import com.neobank.enums.AccountType
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "accounts")
open class Account @JvmOverloads constructor(

    @Column(name = "iban", nullable = false, unique = true, length = 28)
    open var iban: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    open var accountType: AccountType = AccountType.CURRENT,

    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    open var balance: BigDecimal = BigDecimal.ZERO,

    @Column(name = "currency", nullable = false, length = 3)
    open var currency: String = "AZN",

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    open var status: AccountStatus = AccountStatus.ACTIVE,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    open var user: User? = null,

    @OneToMany(mappedBy = "account", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    open var cards: MutableList<Card> = mutableListOf(),

    @OneToMany(mappedBy = "fromAccount", fetch = FetchType.LAZY)
    open var sentTransactions: MutableList<Transaction> = mutableListOf(),

    @OneToMany(mappedBy = "toAccount", fetch = FetchType.LAZY)
    open var receivedTransactions: MutableList<Transaction> = mutableListOf()

) : BaseEntity()