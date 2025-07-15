package com.harshvardhan.quizapp.usecases

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.harshvardhan.quizapp.dataModels.AnsweredQuestion
import com.harshvardhan.quizapp.dataModels.Question

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CalculateStreakUseCaseTest {

    private lateinit var calculateStreakUseCase: CalculateStreakUseCase

    @Before
    fun setUp() {
        calculateStreakUseCase = CalculateStreakUseCase()
    }

    @Test
    fun `calculateLongestStreak returns 0 for empty list`() {
        // Given
        val answeredQuestions = emptyList<AnsweredQuestion>()

        // When
        val result = calculateStreakUseCase.calculateLongestStreak(answeredQuestions)

        // Then
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun `calculateLongestStreak returns 0 when all answers are incorrect`() {
        // Given
        val answeredQuestions = listOf(
            createAnsweredQuestion(isCorrect = false, isSkipped = false),
            createAnsweredQuestion(isCorrect = false, isSkipped = false),
            createAnsweredQuestion(isCorrect = false, isSkipped = false)
        )

        // When
        val result = calculateStreakUseCase.calculateLongestStreak(answeredQuestions)

        // Then
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun `calculateLongestStreak returns 0 when all answers are skipped`() {
        // Given
        val answeredQuestions = listOf(
            createAnsweredQuestion(isCorrect = true, isSkipped = true),
            createAnsweredQuestion(isCorrect = true, isSkipped = true),
            createAnsweredQuestion(isCorrect = true, isSkipped = true)
        )

        // When
        val result = calculateStreakUseCase.calculateLongestStreak(answeredQuestions)

        // Then
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun `calculateLongestStreak returns 1 for single correct answer`() {
        // Given
        val answeredQuestions = listOf(
            createAnsweredQuestion(isCorrect = true, isSkipped = false)
        )

        // When
        val result = calculateStreakUseCase.calculateLongestStreak(answeredQuestions)

        // Then
        assertThat(result).isEqualTo(1)
    }

    @Test
    fun `calculateLongestStreak returns correct streak for consecutive correct answers`() {
        // Given
        val answeredQuestions = listOf(
            createAnsweredQuestion(isCorrect = true, isSkipped = false),
            createAnsweredQuestion(isCorrect = true, isSkipped = false),
            createAnsweredQuestion(isCorrect = true, isSkipped = false),
            createAnsweredQuestion(isCorrect = true, isSkipped = false)
        )

        // When
        val result = calculateStreakUseCase.calculateLongestStreak(answeredQuestions)

        // Then
        assertThat(result).isEqualTo(4)
    }

    @Test
    fun `calculateLongestStreak handles mixed correct and incorrect answers`() {
        // Given
        val answeredQuestions = listOf(
            createAnsweredQuestion(isCorrect = true, isSkipped = false),   // streak: 1
            createAnsweredQuestion(isCorrect = true, isSkipped = false),   // streak: 2
            createAnsweredQuestion(isCorrect = false, isSkipped = false),  // streak: 0
            createAnsweredQuestion(isCorrect = true, isSkipped = false),   // streak: 1
            createAnsweredQuestion(isCorrect = true, isSkipped = false),   // streak: 2
            createAnsweredQuestion(isCorrect = true, isSkipped = false)    // streak: 3
        )

        // When
        val result = calculateStreakUseCase.calculateLongestStreak(answeredQuestions)

        // Then
        assertThat(result).isEqualTo(3)
    }

    @Test
    fun `calculateLongestStreak handles multiple streaks and returns longest`() {
        // Given
        val answeredQuestions = listOf(
            createAnsweredQuestion(isCorrect = true, isSkipped = false),   // streak: 1
            createAnsweredQuestion(isCorrect = true, isSkipped = false),   // streak: 2
            createAnsweredQuestion(isCorrect = true, isSkipped = false),   // streak: 3
            createAnsweredQuestion(isCorrect = false, isSkipped = false),  // streak: 0
            createAnsweredQuestion(isCorrect = true, isSkipped = false),   // streak: 1
            createAnsweredQuestion(isCorrect = true, isSkipped = false),   // streak: 2
            createAnsweredQuestion(isCorrect = false, isSkipped = false),  // streak: 0
            createAnsweredQuestion(isCorrect = true, isSkipped = false),   // streak: 1
            createAnsweredQuestion(isCorrect = true, isSkipped = false),   // streak: 2
            createAnsweredQuestion(isCorrect = true, isSkipped = false),   // streak: 3
            createAnsweredQuestion(isCorrect = true, isSkipped = false)    // streak: 4
        )

        // When
        val result = calculateStreakUseCase.calculateLongestStreak(answeredQuestions)

        // Then
        assertThat(result).isEqualTo(4)
    }

    @Test
    fun `calculateLongestStreak ignores skipped questions even if marked as correct`() {
        // Given
        val answeredQuestions = listOf(
            createAnsweredQuestion(isCorrect = true, isSkipped = false),   // streak: 1
            createAnsweredQuestion(isCorrect = true, isSkipped = false),   // streak: 2
            createAnsweredQuestion(isCorrect = true, isSkipped = true),    // streak: 0 (skipped)
            createAnsweredQuestion(isCorrect = true, isSkipped = false),   // streak: 1
            createAnsweredQuestion(isCorrect = true, isSkipped = false)    // streak: 2
        )

        // When
        val result = calculateStreakUseCase.calculateLongestStreak(answeredQuestions)

        // Then
        assertThat(result).isEqualTo(2)
    }

    @Test
    fun `calculateCurrentStreak returns 0 for empty list`() {
        // Given
        val answeredQuestions = emptyList<AnsweredQuestion>()

        // When
        val result = calculateStreakUseCase.calculateCurrentStreak(answeredQuestions)

        // Then
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun `calculateCurrentStreak returns 0 when last answer is incorrect`() {
        // Given
        val answeredQuestions = listOf(
            createAnsweredQuestion(isCorrect = true, isSkipped = false),
            createAnsweredQuestion(isCorrect = true, isSkipped = false),
            createAnsweredQuestion(isCorrect = false, isSkipped = false)
        )

        // When
        val result = calculateStreakUseCase.calculateCurrentStreak(answeredQuestions)

        // Then
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun `calculateCurrentStreak returns 0 when last answer is skipped`() {
        // Given
        val answeredQuestions = listOf(
            createAnsweredQuestion(isCorrect = true, isSkipped = false),
            createAnsweredQuestion(isCorrect = true, isSkipped = false),
            createAnsweredQuestion(isCorrect = true, isSkipped = true)
        )

        // When
        val result = calculateStreakUseCase.calculateCurrentStreak(answeredQuestions)

        // Then
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun `calculateCurrentStreak returns 1 for single correct answer`() {
        // Given
        val answeredQuestions = listOf(
            createAnsweredQuestion(isCorrect = true, isSkipped = false)
        )

        // When
        val result = calculateStreakUseCase.calculateCurrentStreak(answeredQuestions)

        // Then
        assertThat(result).isEqualTo(1)
    }

    @Test
    fun `calculateCurrentStreak returns correct count for consecutive correct answers at end`() {
        // Given
        val answeredQuestions = listOf(
            createAnsweredQuestion(isCorrect = false, isSkipped = false),
            createAnsweredQuestion(isCorrect = true, isSkipped = false),
            createAnsweredQuestion(isCorrect = true, isSkipped = false),
            createAnsweredQuestion(isCorrect = true, isSkipped = false)
        )

        // When
        val result = calculateStreakUseCase.calculateCurrentStreak(answeredQuestions)

        // Then
        assertThat(result).isEqualTo(3)
    }

    @Test
    fun `calculateCurrentStreak counts only from the last correct sequence`() {
        // Given
        val answeredQuestions = listOf(
            createAnsweredQuestion(isCorrect = true, isSkipped = false),   // old streak
            createAnsweredQuestion(isCorrect = true, isSkipped = false),   // old streak
            createAnsweredQuestion(isCorrect = true, isSkipped = false),   // old streak
            createAnsweredQuestion(isCorrect = false, isSkipped = false),  // break
            createAnsweredQuestion(isCorrect = true, isSkipped = false),   // current: 1
            createAnsweredQuestion(isCorrect = true, isSkipped = false)    // current: 2
        )

        // When
        val result = calculateStreakUseCase.calculateCurrentStreak(answeredQuestions)

        // Then
        assertThat(result).isEqualTo(2)
    }

    @Test
    fun `calculateCurrentStreak handles all correct answers`() {
        // Given
        val answeredQuestions = listOf(
            createAnsweredQuestion(isCorrect = true, isSkipped = false),
            createAnsweredQuestion(isCorrect = true, isSkipped = false),
            createAnsweredQuestion(isCorrect = true, isSkipped = false),
            createAnsweredQuestion(isCorrect = true, isSkipped = false),
            createAnsweredQuestion(isCorrect = true, isSkipped = false)
        )

        // When
        val result = calculateStreakUseCase.calculateCurrentStreak(answeredQuestions)

        // Then
        assertThat(result).isEqualTo(5)
    }

    @Test
    fun `calculateCurrentStreak ignores skipped questions in current streak`() {
        // Given
        val answeredQuestions = listOf(
            createAnsweredQuestion(isCorrect = true, isSkipped = false),
            createAnsweredQuestion(isCorrect = true, isSkipped = false),
            createAnsweredQuestion(isCorrect = true, isSkipped = true),    // breaks streak
            createAnsweredQuestion(isCorrect = true, isSkipped = false),
            createAnsweredQuestion(isCorrect = true, isSkipped = false)
        )

        // When
        val result = calculateStreakUseCase.calculateCurrentStreak(answeredQuestions)

        // Then
        assertThat(result).isEqualTo(2)
    }

    @Test
    fun `calculateCurrentStreak returns 0 when all answers are incorrect`() {
        // Given
        val answeredQuestions = listOf(
            createAnsweredQuestion(isCorrect = false, isSkipped = false),
            createAnsweredQuestion(isCorrect = false, isSkipped = false),
            createAnsweredQuestion(isCorrect = false, isSkipped = false)
        )

        // When
        val result = calculateStreakUseCase.calculateCurrentStreak(answeredQuestions)

        // Then
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun `both methods handle single skipped question correctly`() {
        // Given
        val answeredQuestions = listOf(
            createAnsweredQuestion(isCorrect = true, isSkipped = true)
        )

        // When
        val longestStreak = calculateStreakUseCase.calculateLongestStreak(answeredQuestions)
        val currentStreak = calculateStreakUseCase.calculateCurrentStreak(answeredQuestions)

        // Then
        assertThat(longestStreak).isEqualTo(0)
        assertThat(currentStreak).isEqualTo(0)
    }

    @Test
    fun `both methods handle complex mixed scenario`() {
        // Given
        val answeredQuestions = listOf(
            createAnsweredQuestion(isCorrect = true, isSkipped = false),   // 1
            createAnsweredQuestion(isCorrect = true, isSkipped = false),   // 2
            createAnsweredQuestion(isCorrect = true, isSkipped = false),   // 3
            createAnsweredQuestion(isCorrect = true, isSkipped = false),   // 4 (longest)
            createAnsweredQuestion(isCorrect = false, isSkipped = false),  // break
            createAnsweredQuestion(isCorrect = true, isSkipped = true),    // skipped
            createAnsweredQuestion(isCorrect = true, isSkipped = false),   // 1
            createAnsweredQuestion(isCorrect = true, isSkipped = false),   // 2
            createAnsweredQuestion(isCorrect = true, isSkipped = false)    // 3 (current)
        )

        // When
        val longestStreak = calculateStreakUseCase.calculateLongestStreak(answeredQuestions)
        val currentStreak = calculateStreakUseCase.calculateCurrentStreak(answeredQuestions)

        // Then
        assertThat(longestStreak).isEqualTo(4)
        assertThat(currentStreak).isEqualTo(3)
    }

    // ============ Helper Methods ============

    private fun createAnsweredQuestion(
        isCorrect: Boolean,
        isSkipped: Boolean,
        questionId: Int = 1,
        questionText: String = "Sample Question",
        selectedOptionIndex: Int? = if (isSkipped) null else 0,
        correctOptionIndex: Int = 1
    ): AnsweredQuestion {
        val question = Question(
            id = questionId,
            question = questionText,
            options = listOf("Option A", "Option B", "Option C", "Option D"),
            correctOptionIndex = correctOptionIndex
        )

        return AnsweredQuestion(
            question = question,
            selectedOptionIndex = selectedOptionIndex,
            isCorrect = isCorrect,
            isSkipped = isSkipped
        )
    }
}