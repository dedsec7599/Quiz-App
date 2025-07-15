package com.harshvardhan.quizapp.ui.quizScreen.composables.result

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.harshvardhan.quizapp.R
import com.harshvardhan.quizapp.dataModels.QuizResult
import com.harshvardhan.quizapp.ui.quizScreen.QuizContract.*
import com.harshvardhan.quizapp.ui.theme.CustomSpacing
import com.harshvardhan.quizapp.ui.theme.StreakGold
import com.harshvardhan.quizapp.utils.ContentDescription
import com.harshvardhan.quizapp.utils.accessibilityId
import kotlinx.coroutines.delay

@Composable
fun ResultsView(
    result: QuizResult, onEventSent: (Event) -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
    }

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val starRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f, animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing), repeatMode = RepeatMode.Restart
        ), label = ""
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(CustomSpacing.medium)
            .accessibilityId(ContentDescription.RESULTS_VIEW),
        verticalArrangement = Arrangement.spacedBy(CustomSpacing.medium),
    ) {
        item {
            AnimatedVisibility(
                visible = isVisible, enter = fadeIn() + slideInVertically()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = CustomSpacing.xSmall)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(CustomSpacing.xLarge)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.quiz_completed),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.accessibilityId(ContentDescription.QUIZ_COMPLETED_HEADER)
                        )

                        Spacer(modifier = Modifier.height(CustomSpacing.medium))

                        Text(
                            text = "${result.correctAnswers}/${result.totalQuestions}",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.accessibilityId(ContentDescription.SCORE_DISPLAY)
                        )

                        Text(
                            text = stringResource(R.string.correct_answers),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            modifier = Modifier.accessibilityId(ContentDescription.CORRECT_ANSWERS_LABEL)
                        )
                    }
                }
            }
        }

        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(delayMillis = 300)) + slideInVertically(tween(delayMillis = 300))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(CustomSpacing.small)
                ) {
                    // Streak card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .accessibilityId(ContentDescription.STREAK_CARD),
                        colors = CardDefaults.cardColors(
                            containerColor = if (result.longestStreak >= 3) StreakGold.copy(alpha = 0.1f)
                            else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(CustomSpacing.medium),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (result.longestStreak >= 3) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = StreakGold,
                                        modifier = Modifier
                                            .size(CustomSpacing.xLarge)
                                            .graphicsLayer(rotationZ = starRotation)
                                            .accessibilityId(ContentDescription.STREAK_ICON)
                                    )
                                    Spacer(modifier = Modifier.width(CustomSpacing.xxSmall))
                                }

                                Text(
                                    text = "${result.longestStreak}",
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (result.longestStreak >= 3) StreakGold else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.accessibilityId(ContentDescription.STREAK_VALUE)
                                )
                            }

                            Text(
                                text = stringResource(R.string.best_streak),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.accessibilityId(ContentDescription.STREAK_LABEL)
                            )
                        }
                    }

                    // Skipped card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .accessibilityId(ContentDescription.SKIPPED_CARD),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(CustomSpacing.medium),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${result.skippedQuestions}",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.accessibilityId(ContentDescription.SKIPPED_VALUE)
                            )

                            Text(
                                text = stringResource(R.string.skipped),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.accessibilityId(ContentDescription.SKIPPED_LABEL)
                            )
                        }
                    }
                }
            }
        }

        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(delayMillis = 600)) + slideInVertically(tween(delayMillis = 600))
            ) {
                Text(
                    text = stringResource(R.string.question_review),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(vertical = CustomSpacing.xSmall)
                        .accessibilityId(ContentDescription.QUESTION_REVIEW_HEADER)
                )
            }
        }

        items(result.answeredQuestions) { answeredQuestion ->
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(delayMillis = 800)) + slideInVertically(tween(delayMillis = 800))
            ) {
                QuestionReviewCard(answeredQuestion = answeredQuestion)
            }
        }

        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(delayMillis = 1000)) + slideInVertically(tween(delayMillis = 1000))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(CustomSpacing.small)
                ) {
                    Button(
                        onClick = { onEventSent(Event.RestartQuiz) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .accessibilityId(ContentDescription.RESTART_BUTTON)
                    ) {
                        Text(stringResource(R.string.restart_quiz))
                    }
                }
            }
        }
    }
}