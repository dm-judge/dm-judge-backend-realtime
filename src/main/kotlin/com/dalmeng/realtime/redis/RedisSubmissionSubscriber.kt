package com.dalmeng.realtime.redis

import jakarta.annotation.PostConstruct
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.listener.PatternTopic
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer
import org.springframework.stereotype.Component

@Component
class RedisSubmissionSubscriber(
    connectionFactory: ReactiveRedisConnectionFactory,
    private val submissionEventBus: SubmissionEventBus
) {
    private val container = ReactiveRedisMessageListenerContainer(connectionFactory)

    @PostConstruct
    fun subscribe() {
        val topic = PatternTopic("judge:submission")

        container.receive(topic)
            .map { it.message }
            .subscribe { msg ->
                submissionEventBus.emit(msg)
            }
    }
}
