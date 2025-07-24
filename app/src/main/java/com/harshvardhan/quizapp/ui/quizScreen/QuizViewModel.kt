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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuizViewModel(
    val getQuestionsUseCase: GetQuestionsUseCase,
    val calculateStreakUseCase: CalculateStreakUseCase,
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

            Event.NextQuestion -> moveToNextQuestion(false)
            Event.RestartQuiz -> {
                val currentTopic = currentState.currentTopic
                loadQuestions(currentTopic.url)
                setState { State().copy(currentTopic = currentTopic) }
            }

            is Event.SelectOption -> selectOption(event.optionIndex)
            Event.SkipQuestion -> moveToNextQuestion(true)
        }
    }

    private fun loadQuestions(url: String) {
        viewModelScope.launch {
            try {
                getQuestionsUseCase(url).fold(onSuccess = {
                    setState { copy(questions = it) }
                }, onFailure = {
                    setEffect { Effect.ShowError }
                    Log.e(TAG, it.message ?: "")
                })
            } catch (e: Exception) {
                setEffect { Effect.ShowError }
                Log.e(TAG, e.message ?: "")
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
        val currentQuestion = currentState.questions[currentState.currentQuestionIndex]
        val selectedIndex = currentState.selectedOptionIndex

        val isCorrect = selectedIndex == currentQuestion.correctOptionIndex
        val answeredQuestion = AnsweredQuestion(
            question = currentQuestion,
            selectedOptionIndex = if (skipped) null else selectedIndex,
            isCorrect = isCorrect,
            isSkipped = skipped
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