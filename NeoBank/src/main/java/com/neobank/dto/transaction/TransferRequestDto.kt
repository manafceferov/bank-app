package com.neobank.dto.transaction

import java.math.BigDecimal

open class TransferRequestDto @JvmOverloads constructor(
    open var fromAccountId: Long? = null,
    open var toIban: String? = null,
    open var toCardNumber: String? = null,
    open var amount: BigDecimal? = null,
    open var description: String? = null
)