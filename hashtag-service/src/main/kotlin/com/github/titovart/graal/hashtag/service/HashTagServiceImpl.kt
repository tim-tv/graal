package com.github.titovart.graal.hashtag.service

import com.github.titovart.graal.hashtag.model.HashTag
import com.github.titovart.graal.hashtag.repository.HashTagRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityExistsException
import javax.persistence.EntityNotFoundException

@Service
class HashTagServiceImpl(private val repository: HashTagRepository) : HashTagService {

    @Transactional(readOnly = true)
    override fun findById(id: Long): HashTag {
        return repository.findById(id).orElseThrow {
            throw EntityNotFoundException("HashTag(id=$id) not found.")
        }
    }

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<HashTag> {
        return repository.findAll(pageable)
    }

    @Transactional(readOnly = true)
    override fun findByValue(value: String): HashTag {
        return repository.findByValue(value) ?:
                throw EntityNotFoundException("HashTag(value=$value) not found.")
    }

    @Transactional
    override fun save(hashTag: HashTag): HashTag {
        repository.findByValue(hashTag.value)?.let {
            throw EntityExistsException("HashTag #${hashTag.value} already exists.")
        }

        return repository.save(HashTag(value = hashTag.value))
    }

    @Transactional
    override fun delete(id: Long) {
        repository.deleteById(id)
    }

}