package com.example.flexibeat.aggregators

import com.example.flexibeat.data.AudioFile


object QueueAggregator {
    private lateinit var listener: ((List<AudioFile>, Int) -> Unit)

    fun subscribe(listener: ((List<AudioFile>, Int) -> Unit)) {
        this.listener = listener
    }

    fun publish(queue: List<AudioFile>, idx: Int) {
        listener(queue, idx)
    }
}