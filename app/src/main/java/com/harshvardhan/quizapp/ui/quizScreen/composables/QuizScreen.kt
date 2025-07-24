package com.harshvardhan.quizapp.ui.quizScreen.composables

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.harshvardhan.quizapp.R
import com.harshvardhan.quizapp.dataModels.QuizResult
import com.harshvardhan.quizapp.ui.quizScreen.QuizContract.Effect
import com.harshvardhan.quizapp.ui.quizScreen.QuizContract.Event
import com.harshvardhan.quizapp.ui.quizScreen.QuizContract.State
import com.harshvardhan.quizapp.ui.quizScreen.composables.question.QuestionCard
import com.harshvardhan.quizapp.ui.quizScreen.composables.result.ResultsView
import com.harshvardhan.quizapp.utils.showToast
import kotlinx.coroutines.flow.Flow

@Composable
fun QuizScreen(
    modifier: Modifier, state: State, onEvent: (Event) -> Unit, onEffectSent: Flow<Effect>, onNavigationRequested: (Effect.Navigation) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        onEffectSent.collect { effect ->
            when (effect) {
                is Effect.Navigation -> onNavigationRequested(effect)

                Effect.ShowError -> {
                    showToast(
                        context,
                        context.getString(R.string.something_went_wrong),
                    )
                }

                is Effect.ShowToast -> {
                    showToast(
                        context,
                        effect.message,
                    )
                }
            }
        }
    }

    BackHandler {
        onEvent(Event.OnBackPress)
    }

    Column(modifier) {
        when {
            state.isQuizCompleted -> {
                val result = QuizResult(
                    correctAnswers = state.answeredQuestions.count { it.correctAnswer == it.userAnswer },
                    totalQuestions = state.questions.size,
                    skippedQuestions = state.answeredQuestions.count { it.userAnswer.isBlank() },
                    longestStreak = state.longestStreak,
                    answeredQuestions = state.answeredQuestions
                )

                Log.d("QuizScreen", result.toString())

                ResultsView(
                    result = result,
                    onEventSent = onEvent
                )
            }

            state.questions.isNotEmpty() -> {
                QuestionCard(
                    state = state,
                    onEventSent = onEvent
                )
            }
        }
    }
}