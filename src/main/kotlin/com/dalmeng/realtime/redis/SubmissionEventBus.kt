package com.dalmeng.realtime.redis

import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks

@Component
class SubmissionEventBus {

    private val sink: Sinks.Many<String> =
        Sinks.many().multicast().onBackpressureBuffer()

    fun emit(message: String) {
        sink.tryEmitNext(message)
    }

    fun flux(): Flux<String> = sink.asFlux()
}