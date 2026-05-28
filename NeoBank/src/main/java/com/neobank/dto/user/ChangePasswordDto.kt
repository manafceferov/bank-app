package com.neobank.dto.user

open class ChangePasswordDto @JvmOverloads constructor(
    open var currentPassword: String? = null,
    open var newPassword: String? = null
)