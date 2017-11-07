package com.github.titovart.graal.user.service

import com.github.titovart.graal.user.model.User
import com.github.titovart.graal.user.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

@Service
class UserServiceImpl(private val repository: UserRepository) : UserService {

    @Transactional(readOnly = true)
    override fun findById(userId: Long): User {
        val user = repository.findById(userId)
        return user.orElseThrow { throw EntityNotFoundException("User(id=$userId) not found") }
    }

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<User> {
        return repository.findAll(pageable)
    }

    @Transactional(readOnly = true)
    override fun findByUserName(username: String): User {
        return repository.findByNickName(username) ?:
                throw EntityNotFoundException("User(username=$username) doesn't exist")
    }

    @Transactional
    override fun save(user: User): User {
        return repository.save(user)
    }

    @Transactional
    override fun update(userId: Long, user: User): User {
        val oldUser = repository.findById(userId).orElseThrow {
            throw EntityNotFoundException("User(id=$userId) not found")
        }

        oldUser.email = user.email
        oldUser.nickName = user.nickName

        return repository.save(oldUser)
    }

    @Transactional
    override fun delete(userId: Long) {
        return repository.deleteById(userId)
    }
}