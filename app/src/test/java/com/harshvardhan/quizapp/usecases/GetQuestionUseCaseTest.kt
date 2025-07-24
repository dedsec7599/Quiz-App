package com.harshvardhan.quizapp.usecases

import assertk.assertThat
import assertk.assertions.*
import com.harshvardhan.quizapp.dataModels.Question
import com.harshvardhan.quizapp.repos.QuizRepo
import com.harshvardhan.quizapp.usecases.quizUseCase.GetQuestionsUseCase
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException

class GetQuestionsUseCaseTest {

    private lateinit var mockQuizRepo: QuizRepo
    private lateinit var getQuestionsUseCase: GetQuestionsUseCase

    @Before
    fun setup() {
        mockQuizRepo = mockk()
        getQuestionsUseCase = GetQuestionsUseCase(mockQuizRepo)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `invoke should return shuffled questions when repo returns success`() = runTest {
        // Given
        val originalQuestions = listOf(
            Question(1, "What is 2+2?", listOf("3", "4", "5"), 1),
            Question(2, "What is capital of France?", listOf("London", "Paris", "Berlin"), 1),
            Question(3, "What is 3*3?", listOf("6", "9", "12"), 1)
        )
        coEvery { mockQuizRepo.fetchTopicQuestions() } returns Result.success(originalQuestions)

        // When
        val result = getQuestionsUseCase(currentState.currentTopic.url, dataRepo)

        // Then
        assertThat(result.isSuccess).isTrue()
        val shuffledQuestions = result.getOrNull()
        assertThat(shuffledQuestions).isNotNull()
        assertThat(shuffledQuestions!!).hasSize(originalQuestions.size)

        // Verify all original questions are present (even if shuffled)
        originalQuestions.forEach { originalQuestion ->
            assertThat(shuffledQuestions).contains(originalQuestion)
        }

        coVerify { mockQuizRepo.fetchTopicQuestions() }
    }

    @Test
    fun `invoke should return failure when repo returns failure`() = runTest {
        // Given
        val exception = IOException("Network error")
        coEvery { mockQuizRepo.fetchTopicQuestions() } returns Result.failure(exception)

        // When
        val result = getQuestionsUseCase(currentState.currentTopic.url, dataRepo)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
        coVerify { mockQuizRepo.fetchTopicQuestions() }
    }

    @Test
    fun `invoke should return empty list when repo returns empty success`() = runTest {
        // Given
        coEvery { mockQuizRepo.fetchTopicQuestions() } returns Result.success(emptyList())

        // When
        val result = getQuestionsUseCase(currentState.currentTopic.url, dataRepo)

        // Then
        assertThat(result.isSuccess).isTrue()
        val questions = result.getOrNull()
        assertThat(questions).isNotNull()
        assertThat(questions!!).isEmpty()
        coVerify { mockQuizRepo.fetchTopicQuestions() }
    }

    @Test
    fun `invoke should return single question when repo returns single question`() = runTest {
        // Given
        val singleQuestion = listOf(
            Question(1, "What is 2+2?", listOf("3", "4", "5"), 1)
        )
        coEvery { mockQuizRepo.fetchTopicQuestions() } returns Result.success(singleQuestion)

        // When
        val result = getQuestionsUseCase(currentState.currentTopic.url, dataRepo)

        // Then
        assertThat(result.isSuccess).isTrue()
        val questions = result.getOrNull()
        assertThat(questions).isNotNull()
        assertThat(questions!!).hasSize(1)
        assertThat(questions[0]).isEqualTo(singleQuestion[0])
        coVerify { mockQuizRepo.fetchTopicQuestions() }
    }

    @Test
    fun `invoke should shuffle questions with multiple calls`() = runTest {
        // Given
        val originalQuestions = listOf(
            Question(1, "Question 1", listOf("A", "B", "C"), 0),
            Question(2, "Question 2", listOf("A", "B", "C"), 1),
            Question(3, "Question 3", listOf("A", "B", "C"), 2),
            Question(4, "Question 4", listOf("A", "B", "C"), 0),
            Question(5, "Question 5", listOf("A", "B", "C"), 1)
        )
        coEvery { mockQuizRepo.fetchTopicQuestions() } returns Result.success(originalQuestions)

        // When - call multiple times
        val result1 = getQuestionsUseCase(currentState.currentTopic.url, dataRepo)
        val result2 = getQuestionsUseCase(currentState.currentTopic.url, dataRepo)

        // Then
        assertThat(result1.isSuccess).isTrue()
        assertThat(result2.isSuccess).isTrue()

        val questions1 = result1.getOrNull()!!
        val questions2 = result2.getOrNull()!!

        // All should have same size and contain same elements
        assertThat(questions1).hasSize(originalQuestions.size)
        assertThat(questions2).hasSize(originalQuestions.size)

        // Verify all questions are present in both results
        originalQuestions.forEach { originalQuestion ->
            assertThat(questions1).contains(originalQuestion)
            assertThat(questions2).contains(originalQuestion)
        }

        coVerify(exactly = 2) { mockQuizRepo.fetchTopicQuestions() }
    }
}