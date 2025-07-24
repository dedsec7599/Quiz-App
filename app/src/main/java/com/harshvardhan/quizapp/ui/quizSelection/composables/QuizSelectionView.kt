package com.harshvardhan.quizapp.ui.quizSelection.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import com.harshvardhan.quizapp.R
import com.harshvardhan.quizapp.ui.quizSelection.QuizSelectionContract.*
import com.harshvardhan.quizapp.ui.theme.CustomSpacing

@Composable
fun QuizSelectionView(state: State, onEventSent: (Event) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(CustomSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(CustomSpacing.xSmall)
    ) {
        item {
            Text(
                text = stringResource(R.string.quiz_topics),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
            )
        }

        items(state.topics, key = { it.id }) {
            Card(
                modifier = Modifier
                    .padding(CustomSpacing.medium)
                    .fillMaxWidth()
                    .clickable {
                        onEventSent(Event.OnTopicSelected(it))
                    }, colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ), elevation = CardDefaults.cardElevation(defaultElevation = CustomSpacing.xxSmall)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(CustomSpacing.medium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = it.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Medium,
                    )
                    if(it.isFinished) Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.Green
                    )
                }

                Text(
                    modifier = Modifier.padding(CustomSpacing.medium),
                    text = it.description,
                    style = MaterialTheme.typography.labelSmall,
                )
            }

        }
    }
}