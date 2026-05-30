package com.neobank.dto.transaction

import java.math.BigDecimal
import java.time.LocalDateTime

open class TransactionResponseDto @JvmOverloads constructor(
    open var id: Long? = null,
    open var amount: BigDecimal? = null,
    open var currency: String? = null,
    open var transactionType: String? = null,
    open var description: String? = null,
    open var referenceNumber: String? = null,
    open var fromAccountIban: String? = null,
    open var toAccountIban: String? = null,
    open var createdAt: LocalDateTime? = null,
    )