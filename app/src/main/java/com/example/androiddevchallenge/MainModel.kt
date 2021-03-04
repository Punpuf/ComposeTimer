/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainModel : ViewModel() {

    private val _time = MutableLiveData(emptyList<Int>())
    val time: LiveData<List<Int>> = _time

    fun onTimeNumAdded(num: Int) {
        if (_time.value?.size ?: 0 >= 6 && _time.value?.reversed()?.getOrNull(5) != 0) return

        val newTime = mutableListOf<Int>().apply {
            addAll(_time.value ?: emptyList())
            add(num)
        }
        _time.value = newTime
    }

    fun onTimeNumRemoved() {
        _time.value = _time.value?.dropLast(1)
    }

    private var countDownTimer: CountDownTimer? = null

    private val _isTimerOn = MutableLiveData(false)
    val isTimerOn: LiveData<Boolean> = _isTimerOn

    fun startTimer() {
        if (_isTimerOn.value == false) {
            countDownTimer = object : CountDownTimer(timeToMillis(_time.value ?: emptyList()), 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    _time.value = millisToTime(millisUntilFinished)
                }

                override fun onFinish() {
                    _time.value = millisToTime(0L)
                    _isTimerOn.value = false
                }
            }
            countDownTimer?.start()
            _isTimerOn.value = true
        }
    }

    fun stopTimer() {
        if (_isTimerOn.value == true) {
            countDownTimer?.cancel()
            countDownTimer = null
            _isTimerOn.value = false

            _time.value = emptyList()
        }
    }

    private fun timeToMillis(time: List<Int>): Long {
        val invertedTime = time.reversed()
        d("input is $invertedTime")
        var seconds = 0L
        // second
        seconds += invertedTime.getOrElse(0) { 0 }
        seconds += invertedTime.getOrElse(1) { 0 } * 10
        // minutes
        seconds += invertedTime.getOrElse(2) { 0 } * 60
        seconds += invertedTime.getOrElse(3) { 0 } * 60 * 10
        // hours
        seconds += invertedTime.getOrElse(4) { 0 } * 60 * 60
        seconds += invertedTime.getOrElse(5) { 0 } * 10 * 60 * 60

        d("output is ${seconds * 1000L}")
        return seconds * 1000L
    }

    private fun millisToTime(millis: Long): List<Int> {
        val seconds: Long = millis / 1000
        var remaining = seconds

        val tensOfHours = (remaining / 10 / 60 / 60).toInt()
        remaining -= tensOfHours * 10 * 60 * 60

        val hours: Int = (remaining / 60 / 60).toInt()
        remaining -= hours * 60 * 60

        val tensOfMinutes: Int = (remaining / 10 / 60).toInt()
        remaining -= tensOfMinutes * 10 * 60

        val minutes: Int = (remaining / 60).toInt()
        remaining -= minutes * 60

        val tensOfSeconds: Int = (remaining / 10).toInt()
        remaining -= tensOfSeconds * 10

        return listOf(
            tensOfHours, hours,
            tensOfMinutes, minutes,
            tensOfSeconds, remaining.toInt()
        )
    }
}
