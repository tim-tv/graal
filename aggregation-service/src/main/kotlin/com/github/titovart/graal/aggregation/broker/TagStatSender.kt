package com.github.titovart.graal.aggregation.broker

import com.github.titovart.graal.aggregation.entity.TagStatistic

interface TagStatSender {

    fun send(payload: TagStatistic)
}