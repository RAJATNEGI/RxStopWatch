package com.rajat.rxstopwatch

data class StopWatchTime(
    val time: Long = 0L,
    val state: StopWatchState = StopWatchState.IDLE
)