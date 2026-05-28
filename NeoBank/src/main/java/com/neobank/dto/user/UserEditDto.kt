package com.neobank.dto.user

open class UserEditDto @JvmOverloads constructor(
    open var firstName: String? = null,
    open var lastName: String? = null,
    open var phoneNumber: String? = null
)