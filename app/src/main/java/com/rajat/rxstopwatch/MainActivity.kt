package com.rajat.rxstopwatch

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var compositeDisposable: CompositeDisposable
    private var ms: String="0"
    private var hour: Long = 0L
    private var minutes: Long = 0L
    private var seconds: Long = 0L
    private var startTime: Long = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        compositeDisposable = CompositeDisposable()

        start.clicks().subscribe {
            startTime = System.currentTimeMillis()
            startTimer().subscribe { updateClock(System.currentTimeMillis()) }
        }
            .let { compositeDisposable.add(it) }

        pause.clicks().subscribe {
            Toast.makeText(this, "pause", Toast.LENGTH_SHORT).show()
        }.let { compositeDisposable.add(it) }

        reset.clicks().subscribe {
            Toast.makeText(this, "reset", Toast.LENGTH_SHORT).show()
        }.let { compositeDisposable.add(it) }

    }

    private fun startTimer(): Observable<Long> {
        return Observable.interval(0, 1, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun updateClock(milliSeconds: Long) {
        val time = (milliSeconds - startTime)
        ms = String.format("%02d",((time / 10) % 100))
        seconds = ((time / 1000) % 60)
        minutes = ((time / 1000) / 60) % 60
        hour = ((time/1000)/3600)%60
        clock.text = "$hour:$minutes:$seconds:${ms}"
    }


    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}
