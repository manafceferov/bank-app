package com.neobank.dto.account

import com.neobank.enums.AccountType

open class AccountCreateDto @JvmOverloads constructor(
    open var accountType: AccountType? = null,
    open var currency: String? = "AZN"
)