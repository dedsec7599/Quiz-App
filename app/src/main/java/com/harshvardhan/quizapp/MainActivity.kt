package com.harshvardhan.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.harshvardhan.quizapp.dataModels.Topic
import com.harshvardhan.quizapp.screens.Screens
import com.harshvardhan.quizapp.ui.quizScreen.QuizContract
import com.harshvardhan.quizapp.ui.quizScreen.QuizViewModel
import com.harshvardhan.quizapp.ui.quizScreen.composables.QuizScreen
import com.harshvardhan.quizapp.ui.quizSelection.QuizSelectionContract.*
import com.harshvardhan.quizapp.ui.quizSelection.QuizSelectionViewModel
import com.harshvardhan.quizapp.ui.quizSelection.composables.QuizSelectionScreen
import com.harshvardhan.quizapp.ui.theme.QuizAppTheme
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            QuizAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    NavHost(
                        navController = navController, startDestination = Screens.QuizSelection
                    ) {
                        composable<Screens.QuizSelection> {
                            val viewModel: QuizSelectionViewModel = koinViewModel()
                            val state by viewModel.state.collectAsStateWithLifecycle(
                                minActiveState = Lifecycle.State.RESUMED
                            )

                            QuizSelectionScreen(modifier = Modifier.padding(paddingValues),
                                state = state,
                                onEvent = viewModel::handleEvent,
                                onEffectSent = viewModel.effect,
                                onNavigationRequested = {
                                    when (it) {
                                        is Effect.Navigation.NavigateToQuiz -> navController.navigate(
                                            Screens.Quiz(
                                                id = it.topic.id,
                                                title = it.topic.title,
                                                description = it.topic.description,
                                                url = it.topic.url
                                            )
                                        )
                                    }
                                })
                        }

                        composable<Screens.Quiz> {
                            val viewModel: QuizViewModel = koinViewModel()
                            val state by viewModel.state.collectAsStateWithLifecycle(
                                minActiveState = Lifecycle.State.RESUMED
                            )

                            val arguments = it.toRoute<Screens.Quiz>()

                            val topic = Topic(
                                id = arguments.id,
                                title = arguments.title,
                                description = arguments.description,
                                url = arguments.url
                            )

                            viewModel.handleEvent(QuizContract.Event.FetchQuestions(topic))

                            QuizScreen(
                                modifier = Modifier.padding(paddingValues),
                                state = state,
                                onEvent = viewModel::handleEvent,
                                onEffectSent = viewModel.effect
                            )
                        }
                    }

                }
            }
        }
    }
}