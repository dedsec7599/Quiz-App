package com.harshvardhan.quizapp.utils

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

fun readJsonFromRaw(context: Context, resourceId: Int): String {
    val inputStream = context.resources.openRawResource(resourceId)
    val reader = BufferedReader(InputStreamReader(inputStream))
    val stringBuilder = StringBuilder()

    reader.use { r ->
        r.forEachLine { line ->
            stringBuilder.append(line)
        }
    }

    return stringBuilder.toString()
}