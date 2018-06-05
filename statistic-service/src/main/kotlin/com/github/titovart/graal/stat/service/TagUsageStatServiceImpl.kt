package com.github.titovart.graal.stat.service

import com.github.titovart.graal.stat.domain.TagUsageStatistic
import com.github.titovart.graal.stat.repository.TagUsageStatRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

@Service
class TagUsageStatServiceImpl(private val repository: TagUsageStatRepository) :
    TagUsageStatService {

    @Transactional(readOnly = true)
    override fun findByTag(tag: String): TagUsageStatistic {
        return repository.findByTag(tag)
                ?: throw EntityNotFoundException("TagUsageStatistic(tag=$tag) not found.")
    }

    @Transactional(readOnly = true)
    override fun findTopUsedTag(pageable: Pageable): Page<TagUsageStatistic> {
        return repository.findAllByOrderByCountDesc(pageable)
    }

    @Transactional(readOnly = true)
    override fun findById(id: Long): TagUsageStatistic {
        return repository.findById(id).orElseThrow {
            throw EntityNotFoundException("TagUsageStatistic(id=$id) not found.")
        }
    }

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<TagUsageStatistic> {
        return repository.findAll(pageable)
    }

    @Transactional
    override fun addTagAsUsed(hashTag: String): TagUsageStatistic {
        val existStat = repository.findByTag(hashTag) ?: TagUsageStatistic(hashTag, 0)

        existStat.count += 1
        return repository.save(existStat)
    }

}