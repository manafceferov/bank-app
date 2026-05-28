package com.neobank.dto.user

open class UserResponseDto @JvmOverloads constructor(
    open var id: Long? = null,
    open var firstName: String? = null,
    open var lastName: String? = null,
    open var email: String? = null,
    open var phoneNumber: String? = null,
    open var finCode: String? = null,
    open var role: String? = null,
    open var active: Boolean? = null
)