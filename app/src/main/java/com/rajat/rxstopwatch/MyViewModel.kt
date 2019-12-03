package com.rajat.rxstopwatch

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class MyViewModel : ViewModel() {

    var publish = PublishSubject.create<StopWatchState>()
    private val pause = BehaviorSubject.create<Long>()
    private val resume = BehaviorSubject.createDefault(0L)

    private var stopWatchTime = StopWatchTime()


    fun getCurrentTime():Long{ return pause.value!! }

    fun registerSubject(observable: Observable<StopWatchState>):Disposable = observable.subscribe {publish.onNext(it)}

    fun uiStates(): Observable<StopWatchTime> =
        publish.switchMap { it ->
            when (it) {
                StopWatchState.IDLE -> {
                    Observable.just(StopWatchTime())
                        .doOnNext{stopWatchTime.time = it.time}
                        .doOnNext { pause.onNext(it.time) }
                        .doOnNext { resume.onNext(it.time) }
                }
              StopWatchState.RUNNING -> {
                    startTimer()
                        .doOnNext(pause::onNext)
                        .map { sec -> StopWatchTime(sec, StopWatchState.RUNNING) }
                }
                StopWatchState.PAUSE-> {
                    resume.onNext(pause.value!!)
                    Observable.just(
                        StopWatchTime(
                            pause.value!!,
                            state = StopWatchState.PAUSE
                        )
                    )

                }
            }
        }.hide().observeOn(AndroidSchedulers.mainThread())

    private fun startTimer(): Observable<Long> {
        return Observable.interval(0, 1, TimeUnit.MILLISECONDS)
            .map {  resume.value!! + it }
            .observeOn(AndroidSchedulers.mainThread())
    }
}