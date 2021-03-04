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

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backspace
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androiddevchallenge.ui.components.AppToolbar
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.theme.dotGothicFamily
import com.example.androiddevchallenge.ui.utils.LocalSysUiController
import com.example.androiddevchallenge.ui.utils.SystemUiController
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import java.util.Locale

@ExperimentalAnimationApi
class MainActivity : ComponentActivity() {

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val model: MainModel by viewModels()

        setContent {
            val systemUiController = remember { SystemUiController(window) }
            CompositionLocalProvider(LocalSysUiController provides systemUiController) {
                ProvideWindowInsets {
                    MyTheme {
                        MyApp(model)
                    }
                }
            }
        }
    }
}

@ExperimentalStdlibApi
@ExperimentalAnimationApi
@Composable
fun MyApp(model: MainModel = viewModel()) {
    val isTimerOn: Boolean by model.isTimerOn.observeAsState(initial = false)
    val time: List<Int> by model.time.observeAsState(initial = emptyList())

    Surface(
        // color = MaterialTheme.colors.secondary,
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                AppToolbar()
            },

            floatingActionButton = {
                AppFab(
                    isTimerOn,
                    { model.startTimer() },
                    { model.stopTimer() },
                    modifier = Modifier.navigationBarsPadding(),
                )
            },

            floatingActionButtonPosition = FabPosition.Center,

            content = {
                AppContent(
                    isTimerOn = isTimerOn,
                    time = time,
                    onNumSelected = { model.onTimeNumAdded(it) },
                    onNumDeleted = { model.onTimeNumRemoved() }
                )
            }
        )
    }
}

@ExperimentalStdlibApi
@Composable
fun AppFab(
    isTimerOn: Boolean,
    start: () -> Unit,
    stop: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Crossfade(targetState = isTimerOn) {
        if (!isTimerOn) {
            ExtendedFloatingActionButton(
                onClick = start,
                modifier = modifier,
                icon = { Icon(imageVector = Icons.Rounded.PlayArrow, contentDescription = null) },
                text = {
                    Text(
                        text = (stringResource(R.string.start_timer).uppercase(Locale.getDefault())),
                        style = Typography().button
                    )
                }
            )
        } else {
            ExtendedFloatingActionButton(
                onClick = stop,
                modifier = modifier,
                icon = { Icon(imageVector = Icons.Rounded.Stop, contentDescription = null) },
                text = { Text(text = stringResource(R.string.stop_timer).uppercase(Locale.getDefault())) }
            )
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun AppContent(
    isTimerOn: Boolean,
    time: List<Int>,
    onNumSelected: (Int) -> Unit,
    onNumDeleted: () -> Unit,
) {

    Crossfade(targetState = isTimerOn) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceAround,
        ) {

            TimeText(time = time)
            InputLayout(isTimerOn, onNumSelected, onNumDeleted)
        }
    }
}

@Composable
fun TimeText(time: List<Int>) {
    val invertedTime = time.reversed()
    var timeText = "${invertedTime.getOrElse(5) { 0 }}${invertedTime.getOrElse(4) { 0 }}h"
    timeText += " ${invertedTime.getOrElse(3) { 0 }}${invertedTime.getOrElse(2) { 0 }}m"
    timeText += " ${invertedTime.getOrElse(1) { 0 }}${invertedTime.getOrElse(0) { 0 }}s"

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 32.dp)
            .animateContentSize()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier
                .height(84.dp)
                .fillMaxSize(),
            text = timeText,
            textAlign = TextAlign.Center,
            style = Typography().h2,
            fontWeight = FontWeight.Bold,
            letterSpacing = 4.sp,
            fontFamily = dotGothicFamily,
        )
    }
}

@SuppressLint("ModifierParameter")
@Composable
fun InputLayout(
    isTimerOn: Boolean,
    onNumSelected: (Int) -> Unit,
    onNumDeleted: () -> Unit,
) {
    if (!isTimerOn) {
        d("Timer not ON PAI NAO TAH OUN")

        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(bottom = 64.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val itemModifier = Modifier.weight(1f)

            InputRow {
                NumberText(1, itemModifier) { onNumSelected(it) }
                NumberText(2, itemModifier) { onNumSelected(it) }
                NumberText(3, itemModifier) { onNumSelected(it) }
                NumberText(4, itemModifier) { onNumSelected(it) }
            }

            InputRow {
                NumberText(5, itemModifier) { onNumSelected(it) }
                NumberText(6, itemModifier) { onNumSelected(it) }
                NumberText(7, itemModifier) { onNumSelected(it) }
                NumberText(8, itemModifier) { onNumSelected(it) }
            }

            InputRow {
                NumberText(9, itemModifier) { onNumSelected(it) }
                NumberText(0, itemModifier) { onNumSelected(it) }
                BackspaceButton(itemModifier) { onNumDeleted() }
                Spacer(modifier = itemModifier)
            }
        }
    }
}

@Composable
fun InputRow(
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            //
            .width(300.dp)
            .height(96.dp)
    ) {
        content()
    }
}

@Composable
fun NumberText(
    num: Int,
    modifier: Modifier = Modifier,
    numClicked: (Int) -> Unit,
) {
    Surface(
        shape = CircleShape,
        modifier = modifier
            .clip(CircleShape)
            .clickable { numClicked(num) }
            .padding(16.dp),
    ) {
        Text(
            text = "$num",
            style = Typography().h4,
            textAlign = TextAlign.Center,
            //
            // fontFamily = dotGothicFamily,
        )
    }
}

@Composable
fun BackspaceButton(
    modifier: Modifier = Modifier,
    onNumDeleted: () -> Unit,
) {
    Surface(
        shape = CircleShape,
        modifier = modifier
            .size(92.dp)
            .clip(CircleShape)
            .clickable { onNumDeleted() }
            .padding(16.dp),
    ) {
        Icon(
            imageVector = Icons.Rounded.Backspace,
            contentDescription = stringResource(R.string.backspace),
            modifier = Modifier.requiredSize(36.dp)
        )
    }
}

@ExperimentalStdlibApi
@ExperimentalAnimationApi
@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@ExperimentalStdlibApi
@ExperimentalAnimationApi
@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}

fun d(msg: String) {
    Log.d("Logging JetTimer", msg)
}
