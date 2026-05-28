package com.neobank.dto.card

import java.time.LocalDate

open class CardResponseDto @JvmOverloads constructor(
    open var id: Long? = null,
    open var accountId: Long? = null,
    open var cardNumber: String? = null,
    open var cardHolderName: String? = null,
    open var expiryDate: LocalDate? = null,
    open var cardType: String? = null,
    open var status: String? = null,
    open var cvv: String? = null
)