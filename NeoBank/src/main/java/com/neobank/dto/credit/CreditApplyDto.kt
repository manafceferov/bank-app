package com.neobank.dto.credit

import java.math.BigDecimal

open class CreditApplyDto @JvmOverloads constructor(
    open var accountId: Long? = null,
    open var amount: BigDecimal? = null,
    open var durationMonths: Int? = null
)