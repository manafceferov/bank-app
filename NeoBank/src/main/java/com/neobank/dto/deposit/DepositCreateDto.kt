package com.neobank.dto.deposit

import java.math.BigDecimal

open class DepositCreateDto @JvmOverloads constructor(
    open var accountId: Long? = null,
    open var amount: BigDecimal? = null,
    open var durationMonths: Int? = null
)