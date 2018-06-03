package com.github.titovart.graal.stat.broker

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.titovart.graal.stat.service.TagUsageStatService
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.IOException


@Component
class RabbitTagStatReceiver(
    private val objectMapper: ObjectMapper,
    private val tagUsageStatService: TagUsageStatService
) : TagStatReceiver {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Value("\${rabbit-mq.tags-stat.queue}")
    private lateinit var tagsStatQueue: String

    override fun receive(payload: TagStatistic) {
        logger.info("[$tagsStatQueue] => received $payload")
        payload.tags.distinct().forEach { tagUsageStatService.addTagAsUsed(it) }
    }

    // FIXME: there are no any way to assign value from properties to const val
    @RabbitListener(queues = ["tags-stat-queue"])
    private fun receiceBytes(data: String) {

        val obj = try {
            objectMapper.readValue<TagStatistic>(data, TagStatistic::class.java)
        } catch (exc: IOException) {
            logger.error("[$tagsStatQueue]: Error while parsing message for $data =>", exc)
            return
        }
        receive(obj)
    }
}