package com.example.unfilterednoise.utils.networks

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {

    fun observe(): Flow<Status>

    enum class Status {
        Available, Unavailable
    }
}