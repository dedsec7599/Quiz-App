package com.harshvardhan.quizapp.ui.quizScreen.composables.question

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.harshvardhan.quizapp.R
import com.harshvardhan.quizapp.ui.quizScreen.QuizContract.*
import com.harshvardhan.quizapp.ui.theme.CustomSpacing
import com.harshvardhan.quizapp.ui.theme.StreakGold
import com.harshvardhan.quizapp.utils.HorizontalSpacer
import com.harshvardhan.quizapp.utils.VerticalSpacer
import kotlinx.coroutines.delay

@Composable
fun QuestionCard(
    state: State, onEventSent: (Event) -> Unit
) {
    val totalQuestions = state.questions.size
    val currentQuestion = state.questions[state.currentQuestionIndex]

    LaunchedEffect(state.isAnswerRevealed) {
        if (state.isAnswerRevealed) {
            delay(2000)
            onEventSent(Event.NextQuestion)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(CustomSpacing.medium)
    ) {
        LinearProgressIndicator(
            progress = { (state.currentQuestionIndex + 1) / totalQuestions.toFloat() },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary
        )

        VerticalSpacer(CustomSpacing.medium)

        // Question counter and streak
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(
                    R.string.question_of,
                    state.currentQuestionIndex + 1,
                    totalQuestions
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            // Streak indicator
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(
                    visible = state.currentStreak >= 3,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = stringResource(R.string.streak_badge),
                        tint = StreakGold,
                        modifier = Modifier.size(CustomSpacing.xLarge)
                    )
                }

                HorizontalSpacer(CustomSpacing.xxSmall)

                Text(
                    text = "Streak: ${state.currentStreak}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (state.currentStreak >= 3) StreakGold else MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.7f
                    ),
                    fontWeight = if (state.currentStreak >= 3) FontWeight.Bold else FontWeight.Normal
                )
            }
        }

        VerticalSpacer(CustomSpacing.huge)

        // Question card
        Card(
            modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ), elevation = CardDefaults.cardElevation(defaultElevation = CustomSpacing.xxSmall)
        ) {
            Text(
                text = state.questions[state.currentQuestionIndex].question,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(CustomSpacing.xLarge)
            )
        }

        VerticalSpacer(CustomSpacing.huge)

        // Options
        currentQuestion.options.forEachIndexed { index, option ->
            OptionCard(option = option,
                index = index,
                isSelected = state.selectedOptionIndex == index,
                isCorrect = index == currentQuestion.correctOptionIndex,
                isRevealed = state.isAnswerRevealed,
                onClick = { onEventSent(Event.SelectOption(index)) })

            VerticalSpacer(CustomSpacing.small)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Skip button
        OutlinedButton(
            onClick = { onEventSent(Event.SkipQuestion) },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.isAnswerRevealed.not()
        ) {
            Text(stringResource(R.string.skip_question))
        }
    }
}