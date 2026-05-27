package com.neobank.entity

import com.neobank.enums.Role
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "users")
open class User @JvmOverloads constructor(

    @Column(name = "first_name", nullable = false, length = 100)
    open var firstName: String? = null,

    @Column(name = "last_name", nullable = false, length = 100)
    open var lastName: String? = null,

    @Column(name = "email", nullable = false, unique = true, length = 100)
    open var email: String? = null,

    @Column(name = "password", nullable = false)
    open var userPassword: String? = null,

    @Column(name = "phone_number", length = 20)
    open var phoneNumber: String? = null,

    @Column(name = "fin_code", unique = true, length = 7)
    open var finCode: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    open var role: Role = Role.CUSTOMER,

    @Column(name = "active", nullable = false)
    open var active: Boolean = true,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    open var accounts: MutableList<Account> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    open var credits: MutableList<Credit> = mutableListOf()

) : BaseEntity(), UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority("ROLE_${role.name}"))

    override fun getPassword(): String? = userPassword

    override fun getUsername(): String? = email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = active
}