package com.harshvardhan.quizapp.ui.quizSelection

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harshvardhan.quizapp.dataModels.Topic
import com.harshvardhan.quizapp.ui.quizSelection.QuizSelectionContract.*
import com.harshvardhan.quizapp.ui.quizSelection.QuizSelectionContract.Effect.Navigation.NavigateToQuiz
import com.harshvardhan.quizapp.usecases.dbUseCase.FetchTopicsFromDbUseCase
import com.harshvardhan.quizapp.usecases.dbUseCase.SyncDbTopicsUseCase
import com.harshvardhan.quizapp.usecases.quizSelectionUseCase.GetTopicsUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuizSelectionViewModel(
    val getTopicsUseCase: GetTopicsUseCase,
    val fetchTopicsFromDbUseCase: FetchTopicsFromDbUseCase,
    val syncDbUseCase: SyncDbTopicsUseCase,
): ViewModel() {
    private val TAG = "QuizSelectionViewModel"

    private val _state = MutableStateFlow(State.initialState)
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    val effect = _effect.asSharedFlow()

    init {
        fetchTopics()
    }

    fun handleEvent(event: Event) {
        when(event) {
            is Event.OnTopicSelected -> {
                setEffect { NavigateToQuiz(event.topic) }
            }

            Event.UpdateTopicStatus -> {
                viewModelScope.launch {
                    loadTopicsFromDb()
                }
            }

            Event.OnBackPress -> setEffect { Effect.Navigation.OnBackPress }
        }
    }

    private fun fetchTopics() {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            try {
                fetchNetworkTopics()
            } catch (e: Exception) {
                setState { copy(hasApiFailed = true) }
                Log.e(TAG, "Exception in fetchTopics: ${e.message ?: ""}")
                viewModelScope.launch {
                    loadTopicsFromDb()
                }
            } finally {
                setState { copy(isLoading = false) }
            }
        }
    }

    private suspend fun fetchNetworkTopics() {
        getTopicsUseCase.invoke().fold(
            onSuccess = { networkTopics ->
                syncAndLoadTopicsFromDb(networkTopics)
            },
            onFailure = { networkError ->
                Log.e(TAG, "Network fetch failed: ${networkError.message ?: ""}")
                setEffect { Effect.ShowError }
                loadTopicsFromDb()
            }
        )
    }

    private suspend fun syncAndLoadTopicsFromDb(networkTopics: List<Topic>) {
        syncDbUseCase(networkTopics).fold(
            onSuccess = {
                loadTopicsFromDb()
            },
            onFailure = { syncError ->
                Log.e(TAG, "Error syncing topics to DB: ${syncError.message ?: syncError}")
                setEffect { Effect.ShowError }
            }
        )
    }

    private suspend fun loadTopicsFromDb() {
        val dbTopics = fetchTopicsFromDbUseCase.invoke()
        val mappedTopics = dbTopics.map { entity ->
            Topic(
                id = entity.id,
                title = entity.title,
                description = entity.description,
                url = entity.url,
                isFinished = entity.isCompleted
            )
        }
        setState { copy(topics = mappedTopics) }
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