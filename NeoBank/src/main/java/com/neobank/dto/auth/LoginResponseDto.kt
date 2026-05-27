package com.neobank.dto.auth

open class LoginResponseDto @JvmOverloads constructor(
    open var token: String? = null,
    open var userId: Long? = null,
    open var email: String? = null,
    open var role: String? = null,
    open var firstName: String? = null,
    open var lastName: String? = null
)