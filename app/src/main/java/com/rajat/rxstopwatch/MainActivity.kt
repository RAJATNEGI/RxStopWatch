package com.rajat.rxstopwatch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.merge
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var compositeDisposable: CompositeDisposable
    private var ms: String = ""
    private var hour: String = ""
    private var minutes: String = ""
    private var seconds: String = ""
    private var startTime: Long = 0L
    private var time = 0L

    private lateinit var viewModel: MyViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        compositeDisposable = CompositeDisposable()
        viewModel = ViewModelProviders.of(this).get(MyViewModel::class.java)


        //use of merge operator for detecting the click of buttons
        listOf<Observable<StopWatchState>>(
            start.clicks().map { StopWatchState.RUNNING },
            pause.clicks().map { StopWatchState.PAUSE },
            reset.clicks().map { StopWatchState.IDLE }).merge()
            .let { viewModel.registerSubject(it) }
            .let { compositeDisposable.add(it) }


        viewModel.uiStates()
            .subscribe { updateUI(it) }
            .let { compositeDisposable.add(it) }

    }


    private fun updateUI(stopWatchTime: StopWatchTime) {
        //update the clock time
        updateClock(stopWatchTime.time)

        //update the state of the buttons
        when (stopWatchTime.state) {
            StopWatchState.RUNNING -> {
                start.isEnabled = false
                pause.isEnabled = true
                reset.isEnabled = true
            }
            StopWatchState.IDLE -> {
                pause.isEnabled = false
                reset.isEnabled = false
                start.isEnabled = true
            }
            StopWatchState.PAUSE -> {
                reset.isEnabled = true
                start.isEnabled = true
                pause.isEnabled = false
            }
        }
    }


    private fun updateClock(milliSeconds: Long) {
        time = (milliSeconds - startTime)

        //milliseconds
        ms = String.format("%02d", ((time / 10) % 100))
        //seconds
        seconds = String.format("%02d", ((time / 1000) % 60))
        //minutes
        minutes = String.format("%02d", ((time / 1000) / 60) % 60)
        //hour
        hour = String.format("%02d", ((time / 1000) / 3600) % 60)

        //update the clock time
        clock.text = "$hour:$minutes:$seconds:$ms"
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}
