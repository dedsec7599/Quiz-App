package com.harshvardhan.quizapp.ui

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.*
import com.harshvardhan.quizapp.dataModels.Question
import com.harshvardhan.quizapp.ui.quizScreen.QuizContract
import com.harshvardhan.quizapp.ui.quizScreen.QuizViewModel
import com.harshvardhan.quizapp.usecases.CalculateStreakUseCase
import com.harshvardhan.quizapp.usecases.GetQuestionsUseCase
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class QuizViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var mockGetQuestionsUseCase: GetQuestionsUseCase
    private lateinit var mockCalculateStreakUseCase: CalculateStreakUseCase
    private lateinit var viewModel: QuizViewModel

    private val sampleQuestions = listOf(
        Question(1, "What is 2+2?", listOf("3", "4", "5"), 1),
        Question(2, "Capital of France?", listOf("London", "Paris", "Berlin"), 1),
        Question(3, "What is 3*3?", listOf("6", "9", "12"), 1)
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockGetQuestionsUseCase = mockk()
        mockCalculateStreakUseCase = mockk()

        // Default mock behavior
        every { mockCalculateStreakUseCase.calculateCurrentStreak(any()) } returns 0
        every { mockCalculateStreakUseCase.calculateLongestStreak(any()) } returns 0
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `initial state should be correct`() = runTest {
        // Given
        coEvery { mockGetQuestionsUseCase() } returns Result.success(sampleQuestions)

        // When
        viewModel = QuizViewModel(mockGetQuestionsUseCase, mockCalculateStreakUseCase)
        advanceUntilIdle()

        // Then
        val state = viewModel.currentState
        assertThat(state.isLoading).isFalse()
        assertThat(state.questions).isEqualTo(sampleQuestions)
        assertThat(state.currentQuestionIndex).isEqualTo(0)
        assertThat(state.selectedOptionIndex).isNull()
        assertThat(state.isAnswerRevealed).isFalse()
        assertThat(state.answeredQuestions).isEmpty()
        assertThat(state.currentStreak).isEqualTo(0)
        assertThat(state.longestStreak).isEqualTo(0)
        assertThat(state.isQuizCompleted).isFalse()
    }

    @Test
    fun `selectOption should update state correctly`() = runTest {
        // Given
        coEvery { mockGetQuestionsUseCase() } returns Result.success(sampleQuestions)
        viewModel = QuizViewModel(mockGetQuestionsUseCase, mockCalculateStreakUseCase)
        advanceUntilIdle()

        // When
        viewModel.handleEvent(QuizContract.Event.SelectOption(1))

        // Then
        val state = viewModel.currentState
        assertThat(state.selectedOptionIndex).isEqualTo(1)
        assertThat(state.isAnswerRevealed).isTrue()
    }

    @Test
    fun `selectOption should not update state when answer is already revealed`() = runTest {
        // Given
        coEvery { mockGetQuestionsUseCase() } returns Result.success(sampleQuestions)
        viewModel = QuizViewModel(mockGetQuestionsUseCase, mockCalculateStreakUseCase)
        advanceUntilIdle()

        // When
        viewModel.handleEvent(QuizContract.Event.SelectOption(1))
        val stateAfterFirstSelection = viewModel.currentState
        viewModel.handleEvent(QuizContract.Event.SelectOption(2))

        // Then
        val finalState = viewModel.currentState
        assertThat(finalState.selectedOptionIndex).isEqualTo(stateAfterFirstSelection.selectedOptionIndex)
        assertThat(finalState.isAnswerRevealed).isTrue()
    }

    @Test
    fun `nextQuestion should move to next question correctly`() = runTest {
        // Given
        coEvery { mockGetQuestionsUseCase() } returns Result.success(sampleQuestions)
        every { mockCalculateStreakUseCase.calculateCurrentStreak(any()) } returns 1
        every { mockCalculateStreakUseCase.calculateLongestStreak(any()) } returns 1

        viewModel = QuizViewModel(mockGetQuestionsUseCase, mockCalculateStreakUseCase)
        advanceUntilIdle()

        // When
        viewModel.handleEvent(QuizContract.Event.SelectOption(1)) // Select correct answer
        viewModel.handleEvent(QuizContract.Event.NextQuestion)

        // Then
        val state = viewModel.currentState
        assertThat(state.currentQuestionIndex).isEqualTo(1)
        assertThat(state.selectedOptionIndex).isNull()
        assertThat(state.isAnswerRevealed).isFalse()
        assertThat(state.answeredQuestions).hasSize(1)
        assertThat(state.currentStreak).isEqualTo(1)
        assertThat(state.longestStreak).isEqualTo(1)

        val answeredQuestion = state.answeredQuestions[0]
        assertThat(answeredQuestion.question).isEqualTo(sampleQuestions[0])
        assertThat(answeredQuestion.selectedOptionIndex).isEqualTo(1)
        assertThat(answeredQuestion.isCorrect).isTrue()
        assertThat(answeredQuestion.isSkipped).isFalse()
    }

    @Test
    fun `nextQuestion should complete quiz when reaching last question`() = runTest {
        // Given
        coEvery { mockGetQuestionsUseCase() } returns Result.success(sampleQuestions)
        viewModel = QuizViewModel(mockGetQuestionsUseCase, mockCalculateStreakUseCase)
        advanceUntilIdle()

        // When - go through all questions
        repeat(sampleQuestions.size) {
            viewModel.handleEvent(QuizContract.Event.SelectOption(1))
            viewModel.handleEvent(QuizContract.Event.NextQuestion)
        }

        // Then
        val state = viewModel.currentState
        assertThat(state.isQuizCompleted).isTrue()
        assertThat(state.answeredQuestions).hasSize(sampleQuestions.size)
    }

    @Test
    fun `skipQuestion should mark question as skipped`() = runTest {
        // Given
        coEvery { mockGetQuestionsUseCase() } returns Result.success(sampleQuestions)
        viewModel = QuizViewModel(mockGetQuestionsUseCase, mockCalculateStreakUseCase)
        advanceUntilIdle()

        // When
        viewModel.handleEvent(QuizContract.Event.SkipQuestion)

        // Then
        val state = viewModel.currentState
        assertThat(state.currentQuestionIndex).isEqualTo(1)
        assertThat(state.answeredQuestions).hasSize(1)

        val answeredQuestion = state.answeredQuestions[0]
        assertThat(answeredQuestion.question).isEqualTo(sampleQuestions[0])
        assertThat(answeredQuestion.selectedOptionIndex).isNull()
        assertThat(answeredQuestion.isCorrect).isFalse()
        assertThat(answeredQuestion.isSkipped).isTrue()
    }

    @Test
    fun `restartQuiz should reset state and reload questions`() = runTest {
        // Given
        coEvery { mockGetQuestionsUseCase() } returns Result.success(sampleQuestions)
        viewModel = QuizViewModel(mockGetQuestionsUseCase, mockCalculateStreakUseCase)
        advanceUntilIdle()

        // Progress through some questions
        viewModel.handleEvent(QuizContract.Event.SelectOption(1))
        viewModel.handleEvent(QuizContract.Event.NextQuestion)

        // When
        viewModel.handleEvent(QuizContract.Event.RestartQuiz)
        advanceUntilIdle()

        // Then
        val state = viewModel.currentState
        assertThat(state.currentQuestionIndex).isEqualTo(0)
        assertThat(state.selectedOptionIndex).isNull()
        assertThat(state.isAnswerRevealed).isFalse()
        assertThat(state.answeredQuestions).isEmpty()
        assertThat(state.currentStreak).isEqualTo(0)
        assertThat(state.longestStreak).isEqualTo(0)
        assertThat(state.isQuizCompleted).isFalse()
        assertThat(state.questions).isEqualTo(sampleQuestions)

        coVerify(exactly = 2) { mockGetQuestionsUseCase() }
    }

    @Test
    fun `selectOption with correct answer should update streak correctly`() = runTest {
        // Given
        coEvery { mockGetQuestionsUseCase() } returns Result.success(sampleQuestions)
        every { mockCalculateStreakUseCase.calculateCurrentStreak(any()) } returns 1
        every { mockCalculateStreakUseCase.calculateLongestStreak(any()) } returns 1

        viewModel = QuizViewModel(mockGetQuestionsUseCase, mockCalculateStreakUseCase)
        advanceUntilIdle()

        // When
        viewModel.handleEvent(QuizContract.Event.SelectOption(1)) // Correct answer
        viewModel.handleEvent(QuizContract.Event.NextQuestion)

        // Then
        verify { mockCalculateStreakUseCase.calculateCurrentStreak(any()) }
        verify { mockCalculateStreakUseCase.calculateLongestStreak(any()) }

        val state = viewModel.currentState
        assertThat(state.currentStreak).isEqualTo(1)
        assertThat(state.longestStreak).isEqualTo(1)
    }

    @Test
    fun `selectOption with incorrect answer should still update streak`() = runTest {
        // Given
        coEvery { mockGetQuestionsUseCase() } returns Result.success(sampleQuestions)
        every { mockCalculateStreakUseCase.calculateCurrentStreak(any()) } returns 0
        every { mockCalculateStreakUseCase.calculateLongestStreak(any()) } returns 0

        viewModel = QuizViewModel(mockGetQuestionsUseCase, mockCalculateStreakUseCase)
        advanceUntilIdle()

        // When
        viewModel.handleEvent(QuizContract.Event.SelectOption(0)) // Incorrect answer
        viewModel.handleEvent(QuizContract.Event.NextQuestion)

        // Then
        verify { mockCalculateStreakUseCase.calculateCurrentStreak(any()) }
        verify { mockCalculateStreakUseCase.calculateLongestStreak(any()) }

        val state = viewModel.currentState
        assertThat(state.currentStreak).isEqualTo(0)
        assertThat(state.longestStreak).isEqualTo(0)

        val answeredQuestion = state.answeredQuestions[0]
        assertThat(answeredQuestion.isCorrect).isFalse()
    }

    @Test
    fun `complete quiz flow should work correctly`() = runTest {
        // Given
        coEvery { mockGetQuestionsUseCase() } returns Result.success(sampleQuestions)
        every { mockCalculateStreakUseCase.calculateCurrentStreak(any()) } returnsMany listOf(1, 2, 3)
        every { mockCalculateStreakUseCase.calculateLongestStreak(any()) } returnsMany listOf(1, 2, 3)

        viewModel = QuizViewModel(mockGetQuestionsUseCase, mockCalculateStreakUseCase)
        advanceUntilIdle()

        // When - complete all questions
        repeat(sampleQuestions.size) { index ->
            viewModel.handleEvent(QuizContract.Event.SelectOption(1)) // Always select correct answer
            viewModel.handleEvent(QuizContract.Event.NextQuestion)
        }

        // Then
        val state = viewModel.currentState
        assertThat(state.isQuizCompleted).isTrue()
        assertThat(state.answeredQuestions).hasSize(sampleQuestions.size)
        assertThat(state.currentStreak).isEqualTo(3)
        assertThat(state.longestStreak).isEqualTo(3)

        // All questions should be answered correctly
        state.answeredQuestions.forEach { answeredQuestion ->
            assertThat(answeredQuestion.isCorrect).isTrue()
            assertThat(answeredQuestion.isSkipped).isFalse()
        }
    }

    @Test
    fun `mixed answers and skips should work correctly`() = runTest {
        // Given
        coEvery { mockGetQuestionsUseCase() } returns Result.success(sampleQuestions)
        every { mockCalculateStreakUseCase.calculateCurrentStreak(any()) } returnsMany listOf(1, 0, 0)
        every { mockCalculateStreakUseCase.calculateLongestStreak(any()) } returnsMany listOf(1, 1, 1)

        viewModel = QuizViewModel(mockGetQuestionsUseCase, mockCalculateStreakUseCase)
        advanceUntilIdle()

        // When
        // Question 1: Correct answer
        viewModel.handleEvent(QuizContract.Event.SelectOption(1))
        viewModel.handleEvent(QuizContract.Event.NextQuestion)

        // Question 2: Skip
        viewModel.handleEvent(QuizContract.Event.SkipQuestion)

        // Question 3: Wrong answer
        viewModel.handleEvent(QuizContract.Event.SelectOption(0))
        viewModel.handleEvent(QuizContract.Event.NextQuestion)

        // Then
        val state = viewModel.currentState
        assertThat(state.isQuizCompleted).isTrue()
        assertThat(state.answeredQuestions).hasSize(3)

        // Check individual answers
        assertThat(state.answeredQuestions[0].isCorrect).isTrue()
        assertThat(state.answeredQuestions[0].isSkipped).isFalse()

        assertThat(state.answeredQuestions[1].isCorrect).isFalse()
        assertThat(state.answeredQuestions[1].isSkipped).isTrue()

        assertThat(state.answeredQuestions[2].isCorrect).isFalse()
        assertThat(state.answeredQuestions[2].isSkipped).isFalse()
    }
}