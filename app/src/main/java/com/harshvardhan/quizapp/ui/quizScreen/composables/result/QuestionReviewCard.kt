package com.harshvardhan.quizapp.ui.quizScreen.composables.result

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.harshvardhan.quizapp.R
import com.harshvardhan.quizapp.dataModels.AnsweredQuestion
import com.harshvardhan.quizapp.ui.theme.CorrectGreen
import com.harshvardhan.quizapp.ui.theme.CustomSpacing
import com.harshvardhan.quizapp.ui.theme.IncorrectRed
import com.harshvardhan.quizapp.utils.ContentDescription
import com.harshvardhan.quizapp.utils.HorizontalSpacer
import com.harshvardhan.quizapp.utils.VerticalSpacer
import com.harshvardhan.quizapp.utils.accessibilityId

@Composable
fun QuestionReviewCard(answeredQuestion: AnsweredQuestion) {
    val isSkipped = answeredQuestion.userAnswer.isBlank()
    val isCorrect = answeredQuestion.userAnswer == answeredQuestion.correctAnswer

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSkipped -> MaterialTheme.colorScheme.surface
            isCorrect -> CorrectGreen.copy(alpha = 0.1f)
            else -> IncorrectRed.copy(alpha = 0.1f)
        },
        animationSpec = tween(300),
        label = ""
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .accessibilityId(ContentDescription.REVIEW_CARD),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = CustomSpacing.xxxSmall)
    ) {
        Column(
            modifier = Modifier.padding(CustomSpacing.medium)
        ) {
            // Question
            Text(
                text = answeredQuestion.question.question,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.accessibilityId(ContentDescription.REVIEW_QUESTION_TEXT)
            )

            VerticalSpacer(CustomSpacing.small)

            // User answer
            if (isSkipped) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(CustomSpacing.xLarge)
                            .background(
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = CircleShape
                            )
                            .accessibilityId(ContentDescription.SKIPPED_ICON),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.question_mark),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    HorizontalSpacer(CustomSpacing.xSmall)

                    Text(
                        text = stringResource(R.string.question_skipped),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.accessibilityId(ContentDescription.SKIPPED_ANSWER_LABEL)
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(CustomSpacing.xLarge)
                            .background(
                                color = if (isCorrect) CorrectGreen else IncorrectRed,
                                shape = CircleShape
                            )
                            .accessibilityId(
                                if (isCorrect)
                                    ContentDescription.CORRECT_ICON
                                else
                                    ContentDescription.INCORRECT_ICON
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = null, // already described by ID
                            tint = Color.White,
                            modifier = Modifier.size(CustomSpacing.small)
                        )
                    }

                    Spacer(modifier = Modifier.width(CustomSpacing.xSmall))

                    Text(
                        text = stringResource(
                            R.string.your_answer,
                            answeredQuestion.userAnswer
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.accessibilityId(ContentDescription.USER_ANSWER_LABEL)
                    )
                }
            }

            // Correct answer (if incorrect)
            if (isCorrect.not()) {
                VerticalSpacer(CustomSpacing.xSmall)

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(CustomSpacing.xLarge)
                            .background(
                                color = CorrectGreen,
                                shape = CircleShape
                            )
                            .accessibilityId(ContentDescription.CORRECT_ICON),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(CustomSpacing.small)
                        )
                    }

                    HorizontalSpacer(CustomSpacing.xSmall)

                    Text(
                        text = stringResource(
                            R.string.correct_answer,
                            answeredQuestion.correctAnswer
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = CorrectGreen,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.accessibilityId(ContentDescription.CORRECT_ANSWER_LABEL)
                    )
                }
            }
        }
    }
}