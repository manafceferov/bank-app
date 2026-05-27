package com.neobank.dto.auth

open class LoginRequestDto @JvmOverloads constructor(
    open var email: String? = null,
    open var password: String? = null
)