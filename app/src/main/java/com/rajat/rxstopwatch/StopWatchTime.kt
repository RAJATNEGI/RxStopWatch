package com.rajat.rxstopwatch

data class StopWatchTime(
    var time: Long = 0L,
    var state: StopWatchState = StopWatchState.IDLE
)