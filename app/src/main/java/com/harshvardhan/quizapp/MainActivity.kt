package com.harshvardhan.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.harshvardhan.quizapp.ui.quizScreen.QuizViewModel
import com.harshvardhan.quizapp.ui.quizScreen.composables.QuizScreen
import com.harshvardhan.quizapp.ui.theme.QuizAppTheme
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.getValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewmodel: QuizViewModel by viewModel()

        setContent {
            val state by viewmodel.state.collectAsStateWithLifecycle(minActiveState = Lifecycle.State.RESUMED)

            QuizAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    QuizScreen(
                        modifier = Modifier.padding(it),
                        state = state,
                        onEvent = viewmodel::handleEvent,
                        onEffectSent = viewmodel.effect
                    )
                }
            }
        }
    }
}