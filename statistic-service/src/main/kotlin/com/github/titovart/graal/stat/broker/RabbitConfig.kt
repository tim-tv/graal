package com.github.titovart.graal.stat.broker

import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory


@Configuration
class RabbitConfig : RabbitListenerConfigurer {


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

    override fun configureRabbitListeners(registrar: RabbitListenerEndpointRegistrar?) {
        registrar!!.messageHandlerMethodFactory = messageHandlerMethodFactory()
    }

    @Bean
    fun messageHandlerMethodFactory(): MessageHandlerMethodFactory {
        val messageHandlerMethodFactory = DefaultMessageHandlerMethodFactory()
        messageHandlerMethodFactory.setMessageConverter(consumerJackson2MessageConverter())
        return messageHandlerMethodFactory
    }

    @Bean
    fun consumerJackson2MessageConverter(): MappingJackson2MessageConverter {
        return MappingJackson2MessageConverter()
    }
}