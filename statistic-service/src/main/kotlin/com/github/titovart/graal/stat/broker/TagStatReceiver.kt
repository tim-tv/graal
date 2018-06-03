package com.github.titovart.graal.stat.broker

data class TagStatistic(val username: String, val tags: Collection<String>)

interface TagStatReceiver {

    fun receive(payload: TagStatistic)
}