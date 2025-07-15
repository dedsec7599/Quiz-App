package com.harshvardhan.quizapp.ui.quizScreen.composables.question

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.harshvardhan.quizapp.R
import com.harshvardhan.quizapp.ui.theme.CorrectGreen
import com.harshvardhan.quizapp.ui.theme.CustomSpacing
import com.harshvardhan.quizapp.ui.theme.IncorrectRed

@Composable
fun OptionCard(
    option: String,
    index: Int,
    isSelected: Boolean,
    isCorrect: Boolean,
    isRevealed: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = animateColorAsState(
        targetValue = when {
            isRevealed && isCorrect -> CorrectGreen.copy(alpha = 0.1f)
            isRevealed && isSelected && !isCorrect -> IncorrectRed.copy(alpha = 0.1f)
            isSelected && !isRevealed -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(300),
        label = ""
    )

    val borderColor = animateColorAsState(
        targetValue = when {
            isRevealed && isCorrect -> CorrectGreen
            isRevealed && isSelected && !isCorrect -> IncorrectRed
            isSelected && !isRevealed -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        },
        animationSpec = tween(300),
        label = ""
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isRevealed) { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor.value),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) CustomSpacing.xSmall else CustomSpacing.xxxSmall
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = CustomSpacing.xxxSmall,
                    color = borderColor.value,
                    shape = RoundedCornerShape(CustomSpacing.xSmall)
                )
                .padding(CustomSpacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(CustomSpacing.huge)
                    .background(
                        color = when {
                            isRevealed && isCorrect -> CorrectGreen
                            isRevealed && isSelected && !isCorrect -> IncorrectRed
                            isSelected && !isRevealed -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isRevealed && isCorrect) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(R.string.correct),
                        tint = Color.White,
                        modifier = Modifier.size(CustomSpacing.medium)
                    )
                } else if (isRevealed && isSelected && !isCorrect) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.incorrect),
                        tint = Color.White,
                        modifier = Modifier.size(CustomSpacing.medium)
                    )
                } else {
                    Text(
                        text = ('A' + index).toString(),
                        color = if (isSelected && !isRevealed) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(CustomSpacing.medium))

            Text(
                text = option,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
    }
}