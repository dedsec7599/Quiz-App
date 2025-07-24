package com.harshvardhan.quizapp.ui.quizScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harshvardhan.quizapp.dataModels.AnsweredQuestion
import com.harshvardhan.quizapp.ui.quizScreen.QuizContract.Effect
import com.harshvardhan.quizapp.ui.quizScreen.QuizContract.Event
import com.harshvardhan.quizapp.ui.quizScreen.QuizContract.State
import com.harshvardhan.quizapp.usecases.quizUseCase.CalculateStreakUseCase
import com.harshvardhan.quizapp.usecases.quizUseCase.GetQuestionsUseCase
import com.harshvardhan.quizapp.usecases.quizUseCase.LoadReviewQuestionsUseCase
import com.harshvardhan.quizapp.usecases.quizUseCase.SaveQuizResultUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuizViewModel(
    private val getQuestionsUseCase: GetQuestionsUseCase,
    private val calculateStreakUseCase: CalculateStreakUseCase,
    private val saveQuizResultUseCase: SaveQuizResultUseCase,
    private val loadReviewQuestionsUseCase: LoadReviewQuestionsUseCase,
) : ViewModel() {
    private val TAG = "QuizViewModel"

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    val effect = _effect.asSharedFlow()

    val currentState
        get() = _state.value


    fun handleEvent(event: Event) {
        when (event) {
            is Event.FetchQuestions -> {
                setState { copy(currentTopic = event.topic) }
                loadQuestions(event.topic.url)
            }

            is Event.ShowReview -> {
                setState { copy(currentTopic = event.topic, showReview = true) }
                loadReviewQuestions()
            }

            Event.NextQuestion -> moveToNextQuestion(false)

            is Event.SelectOption -> selectOption(event.optionIndex)

            Event.SkipQuestion -> moveToNextQuestion(true)

            Event.OnBackPress -> setEffect { Effect.Navigation.OnBackPress }
        }
    }

    private fun loadQuestions(url: String) {
        viewModelScope.launch {
            try {
                getQuestionsUseCase(url).fold(onSuccess = {
                    setState { copy(questions = it) }
                }, onFailure = {
                    setState { copy(showReview = true) }
                    setEffect { Effect.ShowError }
                    Log.e(TAG, it.message ?: "")
                    loadReviewQuestions()
                })
            } catch (e: Exception) {
                setState { copy(showReview = true) }
                setEffect { Effect.ShowError }
                Log.e(TAG, e.message ?: "")
                loadReviewQuestions()
            }
        }
    }

    private fun finishQuizAndSave() {
        viewModelScope.launch {
            val topicId = currentState.currentTopic.id
            val bestStreak = currentState.longestStreak
            val userAnswers = currentState.answeredQuestions.associate { answered ->
                answered.question.id to answered.userAnswer
            }
            val networkQuestions = currentState.questions

            try {
                saveQuizResultUseCase(topicId, networkQuestions, userAnswers, bestStreak)
                setEffect { Effect.ShowToast("Quiz saved successfully!") }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save quiz data: ${e.message}")
                setEffect { Effect.ShowError }
            }
        }
    }

    private fun loadReviewQuestions() {
        viewModelScope.launch {
            try {
                val questionPairs = loadReviewQuestionsUseCase(currentState.currentTopic.id)
                val questionsForReview = questionPairs.map { it.first }
                val answeredQuestions = questionPairs.map { it.second }

                setState {
                    copy(
                        questions = questionsForReview,
                        isQuizCompleted = true,
                        currentQuestionIndex = 0,
                        answeredQuestions = answeredQuestions,
                        longestStreak = currentState.currentTopic.bestStreak
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load questions for review: ${e.message}")
                setEffect { Effect.ShowError }
                setEffect { Effect.ShowToast("Couldn't load questions") }
                setEffect { Effect.Navigation.OnBackPress }
            }
        }
    }

    private fun selectOption(optionIndex: Int) {
        if (currentState.isAnswerRevealed) return

        setState {
            copy(
                selectedOptionIndex = optionIndex, isAnswerRevealed = true
            )
        }
    }

    private fun moveToNextQuestion(skipped: Boolean) {
        if (currentState.currentQuestionIndex >= currentState.questions.size) {
            return
        }

        val currentQuestion = currentState.questions[currentState.currentQuestionIndex]
        val selectedIndex = currentState.selectedOptionIndex ?: 0
        val userAnswer = if (skipped) "" else currentQuestion.options[selectedIndex]
        val correctAnswer = currentQuestion.options[currentQuestion.correctOptionIndex]

        val answeredQuestion = AnsweredQuestion(
            question = currentQuestion,
            userAnswer = userAnswer,
            correctAnswer = correctAnswer,
        )

        val updatedAnsweredQuestions = currentState.answeredQuestions + answeredQuestion
        val newCurrentStreak =
            calculateStreakUseCase.calculateCurrentStreak(updatedAnsweredQuestions)
        val newLongestStreak =
            calculateStreakUseCase.calculateLongestStreak(updatedAnsweredQuestions)

        setState {
            copy(
                answeredQuestions = updatedAnsweredQuestions,
                currentStreak = newCurrentStreak,
                longestStreak = newLongestStreak,
                selectedOptionIndex = null,
                isAnswerRevealed = false
            )
        }
        proceedToNextQuestion()
    }

    private fun proceedToNextQuestion() {
        val nextIndex = currentState.currentQuestionIndex + 1

        if (nextIndex >= currentState.questions.size) {
            setState { copy(isQuizCompleted = true) }
            finishQuizAndSave()
        } else {
            setState { copy(currentQuestionIndex = nextIndex) }
        }
    }

    private fun setState(update: State.() -> State) {
        _state.update { currentState ->
            currentState.update()
        }
    }

    private fun setEffect(effect: () -> Effect) {
        val effectValue = effect()
        viewModelScope.launch { _effect.emit(effectValue) }
    }
}