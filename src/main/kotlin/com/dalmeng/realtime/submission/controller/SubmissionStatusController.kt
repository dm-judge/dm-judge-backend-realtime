package com.dalmeng.realtime.submission.controller

import com.dalmeng.realtime.redis.SubmissionEventBus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@CrossOrigin(origins = ["*"])
@RestController
class SubmissionStatusController(
    private val submissionEventBus: SubmissionEventBus
) {

    @GetMapping(
        "/submission-status",
        produces = [MediaType.TEXT_EVENT_STREAM_VALUE]
    )
    fun stream(): Flux<String> {
        return submissionEventBus.flux()
    }
}