package com.github.titovart.graal.aggregation.broker

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.titovart.graal.aggregation.entity.TagStatistic
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component


@Component
class RabbitTagStatSender(private val rabbitTemplate: AmqpTemplate) : TagStatSender {

    @Value("\${rabbit-mq.tags-stat.exchange}")
    private lateinit var tagsStatExchange: String

    @Value("\${rabbit-mq.tags-stat.routing-key}")
    private lateinit var tagsStatRoutingKey: String

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    override fun send(payload: TagStatistic) {

        val obj = objectMapper.writeValueAsString(payload)

        rabbitTemplate.convertAndSend(tagsStatExchange, tagsStatRoutingKey, obj)
    }
}