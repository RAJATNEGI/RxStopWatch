package com.rajat.rxstopwatch

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.merge
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var timeDisposable:Disposable
    private var pauseState = BehaviorSubject.create<Long>()
    private var ms: String = "0"
    private var hour: String = "0"
    private var minutes: String = "0"
    private var seconds: String = "0"
    private var startTime: Long = 0L
    private lateinit var viewModel:MyViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        compositeDisposable = CompositeDisposable()
        viewModel = ViewModelProviders.of(this).get(MyViewModel::class.java)

        listOf<Observable<StopWatchState>>(
            start.clicks().map { StopWatchState.RUNNING },
            pause.clicks().map { StopWatchState.PAUSE },
            reset.clicks().map { StopWatchState.IDLE }).merge()
            .let { viewModel.registerSubject(it) }
            .let { compositeDisposable.add(it) }


        viewModel.uiStates()
            .subscribe{updateUI(it)}
            .let { compositeDisposable.add(it) }

//        start.clicks().subscribe {
//            startTime = System.currentTimeMillis()
//         timeDisposable =    startTimer().subscribe ({ updateClock(System.currentTimeMillis())},
//             {},
//             {Toast.makeText(this,"onComplete",Toast.LENGTH_SHORT).show()})
//
//        }.let { compositeDisposable.add(it) }
//
//        pause.clicks().subscribe {
//            Toast.makeText(this, "pause", Toast.LENGTH_SHORT).show()
//            timeDisposable.dispose()
//        }.let { compositeDisposable.add(it) }
//
//        reset.clicks().subscribe {
//            Toast.makeText(this, "reset", Toast.LENGTH_SHORT).show()
//        }.let { compositeDisposable.add(it) }

    }


    private fun updateUI(stopWatchTime: StopWatchTime){
        updateClock(stopWatchTime.time)
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
            StopWatchState.PAUSE-> {
                reset.isEnabled = true
                start.isEnabled = true
                pause.isEnabled = false
            }
        }
    }


    private fun updateClock(milliSeconds: Long) {
        val time = (milliSeconds - startTime)
        ms = String.format("%02d", ((time / 10) % 100))
        seconds = String.format("%02d", ((time / 1000) % 60))
        minutes = String.format("%02d", ((time / 1000) / 60) % 60)
        hour = String.format("%02d", ((time / 1000) / 3600) % 60)
        clock.text = "$hour:$minutes:$seconds:$ms"
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}
