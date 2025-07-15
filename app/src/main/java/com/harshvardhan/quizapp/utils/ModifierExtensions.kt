package com.harshvardhan.quizapp.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

@Composable
fun Modifier.accessibilityId(id: String) = this.semantics { contentDescription = id }