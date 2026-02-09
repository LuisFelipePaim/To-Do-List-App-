package com.example.todolist.ui.feature.addedit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.TodoRepository
import com.example.todolist.domain.Todo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditViewModel(
    private val repository: TodoRepository,
    private val todoId: String?
) : ViewModel() {

    var title by mutableStateOf("")
        private set

    var description by mutableStateOf("")
        private set

    private var currentIsDone = false

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        if (todoId != null) {
            viewModelScope.launch {
                val result = repository.getTodoById(todoId)
                if (result.isSuccess) {
                    result.getOrNull()?.let { todo ->
                        title = todo.title
                        description = todo.description ?: ""
                        currentIsDone = todo.isCompleted
                    }
                }
            }
        }
    }

    fun onEvent(event: AddEditEvent) {
        when (event) {
            is AddEditEvent.OnTitleChange -> title = event.title
            is AddEditEvent.OnDescriptionChange -> description = event.description
            is AddEditEvent.OnSaveTodo -> saveTodo()
            is AddEditEvent.OnNavigateBack -> {
                viewModelScope.launch { _uiEvent.send(UiEvent.PopBackStack) }
            }
        }
    }

    private fun saveTodo() {
        viewModelScope.launch {
            if (title.isBlank()) {
                _uiEvent.send(UiEvent.ShowSnackbar("O título não pode estar vazio"))
                return@launch
            }

            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            if (userId.isBlank()) {
                _uiEvent.send(UiEvent.ShowSnackbar("Erro: Usuário não logado."))
                return@launch
            }

            val result = if (todoId != null) {
                val updatedTodo = Todo(
                    id = todoId,
                    title = title,
                    description = description,
                    isCompleted = currentIsDone,
                    userId = userId
                )
                repository.updateTodo(updatedTodo)
            } else {
                val newTodo = Todo(
                    id = "",
                    title = title,
                    description = description,
                    isCompleted = false,
                    userId = userId
                )
                repository.addTodo(newTodo)
            }

            if (result.isSuccess) {
                _uiEvent.send(UiEvent.PopBackStack)
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Erro desconhecido ao salvar"
                _uiEvent.send(UiEvent.ShowSnackbar("Erro: $errorMsg"))
            }
        }
    }
}

sealed class UiEvent {
    object PopBackStack : UiEvent()
    data class ShowSnackbar(val message: String) : UiEvent()
}

class AddEditViewModelFactory(
    private val repository: TodoRepository,
    private val todoId: String?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddEditViewModel(repository, todoId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}