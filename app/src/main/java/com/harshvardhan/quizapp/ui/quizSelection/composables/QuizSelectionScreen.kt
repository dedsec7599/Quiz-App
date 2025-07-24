package com.harshvardhan.quizapp.ui.quizSelection.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.harshvardhan.quizapp.R
import com.harshvardhan.quizapp.ui.quizSelection.QuizSelectionContract.*
import com.harshvardhan.quizapp.utils.showToast
import kotlinx.coroutines.flow.Flow

@Composable
fun QuizSelectionScreen(
    modifier: Modifier, state: State, onEvent: (Event) -> Unit, onEffectSent: Flow<Effect>, onNavigationRequested: (Effect.Navigation) -> Unit
) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        onEffectSent.collect { effect ->
            when (effect) {
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

                is Effect.Navigation -> onNavigationRequested(effect)
            }
        }
    }

    Column(modifier) {
        when {
            state.isLoading -> {
                LoadingView()
            }

            state.topics.isNotEmpty() -> QuizSelectionView(
                state = state,
                onEventSent = onEvent
            )
        }
    }
}