package com.neobank.dto.account

import java.math.BigDecimal

open class AccountResponseDto @JvmOverloads constructor(
    open var id: Long? = null,
    open var iban: String? = null,
    open var accountType: String? = null,
    open var balance: BigDecimal? = null,
    open var currency: String? = null,
    open var status: String? = null
)