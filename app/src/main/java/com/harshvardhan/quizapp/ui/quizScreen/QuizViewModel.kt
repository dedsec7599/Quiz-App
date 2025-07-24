package com.harshvardhan.quizapp.ui.quizScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harshvardhan.quizapp.dataModels.AnsweredQuestion
import com.harshvardhan.quizapp.dataModels.Question
import com.harshvardhan.quizapp.repos.QuizDBRepo
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
    private val quizDbRepo: QuizDBRepo
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
            Event.OnBackPress -> setEffect { Effect.Navigation.OnBackPress }
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
                    loadReviewQuestions()
                })
            } catch (e: Exception) {
                setEffect { Effect.ShowError }
                Log.e(TAG, e.message ?: "")
                loadReviewQuestions()
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
            // Prevent out-of-bounds access: maybe ignore or signal quiz complete
            return
        }

        val currentQuestion = currentState.questions[currentState.currentQuestionIndex]
        val selectedIndex = currentState.selectedOptionIndex ?: 0
        val userAnswer = if(skipped) "" else currentQuestion.options[selectedIndex]
        val correctAnswer  = currentQuestion.options[currentQuestion.correctOptionIndex]

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
            finishQuizAndSave()   // <-- Trigger DB save and validation
        } else {
            setState { copy(currentQuestionIndex = nextIndex) }
        }
    }

    private fun finishQuizAndSave() {
        viewModelScope.launch {
            val topicId = currentState.currentTopic.id

            // Build map of user answers: question id -> selected text; "" if not answered
            val userAnswers = currentState.answeredQuestions.map { answered ->
                answered.question.id to answered.userAnswer
            }.toMap()

            val networkQuestions = currentState.questions

            try {
                val savedQuestions = quizDbRepo.getAllQuestionsForTopic(topicId).map {
                    Question(
                        id = it.id,
                        question = it.question,
                        options = listOf(it.correctAnswer), // Or load real options if available in DB
                        correctOptionIndex = 0
                    )
                }

                if (savedQuestions.isEmpty()) {
                    // First attempt: save answers and mark as completed
                    quizDbRepo.completeTopicAndSaveQuestions(topicId, networkQuestions, userAnswers)
                    setEffect { Effect.ShowToast("Quiz saved successfully!") }
                    return@launch
                }

                // Compare IDs and content (expand as needed)
                val idsMatch = savedQuestions.map { it.id }.toSet() == networkQuestions.map { it.id }.toSet()
                val contentMatch = savedQuestions.zip(networkQuestions) { saved, network ->
                    saved.question == network.question &&
                            saved.options == network.options
                }.all { it }

                if (!idsMatch || !contentMatch) {
                    // Questions differ
                    quizDbRepo.syncTopics(listOf(currentState.currentTopic.copy(isFinished = false)))
                    return@launch
                }

                // Everything matches: save answers and mark as completed
                quizDbRepo.completeTopicAndSaveQuestions(topicId, networkQuestions, userAnswers)
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
                val savedQuestions = quizDbRepo.getAllQuestionsForTopic(currentState.currentTopic.id)

                val questionsForReview = savedQuestions.map { questionEntity ->
                    Question(
                        id = questionEntity.id,
                        question = questionEntity.question,
                        options = listOf(), // Adjust if you store options
                        correctOptionIndex = 0
                    )
                }

                if(savedQuestions.isEmpty()) {
                    setEffect { Effect.ShowToast("Couldn't load questions") }
                    setEffect { Effect.Navigation.OnBackPress }
                    return@launch
                }

                setState {
                    copy(
                        questions = questionsForReview,
                        isQuizCompleted = true,
                        currentQuestionIndex = 0,
                        answeredQuestions = savedQuestions.map { entity ->
                            AnsweredQuestion(
                                question = Question(
                                    id = entity.id,
                                    question = entity.question,
                                    options = listOf(),
                                    correctOptionIndex = 0
                                ),
                                correctAnswer = entity.correctAnswer,
                                userAnswer = entity.userAnswer ?: ""
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load questions for review: ${e.message}")
                setEffect { Effect.ShowError }
            }
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