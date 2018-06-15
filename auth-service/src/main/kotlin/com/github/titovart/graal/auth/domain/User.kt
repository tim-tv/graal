package com.github.titovart.graal.auth.domain

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.*
import kotlin.jvm.Transient


@Entity
@Table(name = "users")
class User : UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = -1L

    @Column(unique = true, nullable = false)
    private lateinit var username: String

    @Column(unique = true, nullable = false)
    private lateinit var email: String

    @Column
    private lateinit var password: String

    @Embedded
    private var authorities: MutableList<out GrantedAuthority> = mutableListOf()

    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinTable(
        name = "users_roles",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")]
    )
    private var roles = mutableListOf<Role>()

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return authorities
    }

    fun setAuthorities(authorities: MutableList<out GrantedAuthority>) {
        this.authorities = authorities
    }

    fun setUsername(username: String) {
        this.username = username
    }

    fun setPassword(password: String) {
        this.password = password
    }

    fun getRoles() = roles.toList()

    fun setRoles(roles: List<Role>) {
        this.roles = roles.toMutableList()
    }

    fun getEmail() = email

    override fun isEnabled(): Boolean = true

    override fun getUsername(): String = username

    override fun isCredentialsNonExpired(): Boolean = true

    override fun getPassword(): String = password

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    companion object {
        fun create(username: String, email: String, password: String): User {
            val user = User()
            user.username = username
            user.email = email
            user.password = password
            user.setRoles(listOf(Role("ROLE_USER")))
            return user
        }

        fun createAdmin(username: String, email: String, password: String): User {
            val user = create(username, email, password)
            user.setRoles(user.getRoles().plus(Role("ROLE_ADMIN")))
            return user
        }
    }
}