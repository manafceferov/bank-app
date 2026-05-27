package com.neobank.dto.deposit

import java.math.BigDecimal
import java.time.LocalDate

open class DepositResponseDto @JvmOverloads constructor(
    open var id: Long? = null,
    open var amount: BigDecimal? = null,
    open var interestRate: BigDecimal? = null,
    open var durationMonths: Int? = null,
    open var startDate: LocalDate? = null,
    open var endDate: LocalDate? = null,
    open var expectedProfit: BigDecimal? = null,
    open var status: String? = null
)