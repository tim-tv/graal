package com.github.titovart.graal.aggregation.broker


import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class RabbitConfig {

    @Value("\${rabbit-mq.tags-stat.queue}")
    private lateinit var tagsStatQueue: String

    @Value("\${rabbit-mq.tags-stat.exchange}")
    private lateinit var tagsStatExchange: String

    @Value("\${rabbit-mq.tags-stat.routing-key}")
    private lateinit var tagsStatRoutingKey: String

    @Bean
    fun tagsStatQueue() = Queue(tagsStatQueue, false)

    @Bean
    fun tagsStatExchange() = TopicExchange(tagsStatExchange)

    @Bean
    fun binding(queue: Queue, exchange: TopicExchange) =
        BindingBuilder.bind(queue).to(exchange).with(tagsStatRoutingKey)

    @Bean
    fun jsonMessageConverter(): MessageConverter {
        return Jackson2JsonMessageConverter()
    }

    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory): AmqpTemplate {
        val rabbiTemplate = RabbitTemplate(connectionFactory)
        rabbiTemplate.messageConverter = jsonMessageConverter()
        return rabbiTemplate
    }


}