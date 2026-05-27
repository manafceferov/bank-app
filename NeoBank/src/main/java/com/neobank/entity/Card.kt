package com.neobank.entity

import com.neobank.enums.CardStatus
import com.neobank.enums.CardType
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "cards")
open class Card @JvmOverloads constructor(

    @Column(name = "card_number", unique = true, length = 16)
    open var cardNumber: String? = null,

    @Column(name = "card_holder_name", nullable = false)
    open var cardHolderName: String? = null,

    @Column(name = "expiry_date")
    open var expiryDate: LocalDate? = null,

    @Column(name = "cvv", length = 3)
    open var cvv: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = false)
    open var cardType: CardType = CardType.VIRTUAL,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    open var status: CardStatus = CardStatus.PENDING,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    open var account: Account? = null

) : BaseEntity()