package com.neobank.dto.card

import com.neobank.enums.CardType

open class CardRequestDto @JvmOverloads constructor(
    open var accountId: Long? = null,
    open var cardType: CardType? = null
)