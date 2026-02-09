package com.example.todolist.ui.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.TodoRepository
import com.example.todolist.domain.Todo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListViewModel(
    private val repository: TodoRepository,
    private val userId: String
) : ViewModel() {

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos.asStateFlow()

    init {
        // Carrega na primeira vez que o ViewModel nasce
        loadTodos()
    }

    // TORNAMOS PÚBLICA (fun) para a tela poder chamar no onResume
    fun loadTodos() {
        viewModelScope.launch {
            val result = repository.getTodos(userId)
            if (result.isSuccess) {
                _todos.value = result.getOrDefault(emptyList())
            }
        }
    }

    fun toggleTodo(todo: Todo) {
        viewModelScope.launch {
            // Atualização Otimista (Muda na hora na tela)
            val currentList = _todos.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == todo.id }
            if (index != -1) {
                currentList[index] = todo
                _todos.value = currentList
            }
            // Salva no banco
            repository.updateTodo(todo)
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            // Remove da lista localmente
            val currentList = _todos.value.toMutableList()
            currentList.removeIf { it.id == todo.id }
            _todos.value = currentList

            // Remove do banco
            repository.deleteTodo(todo.id)
        }
    }
}

class ListViewModelFactory(
    private val repository: TodoRepository,
    private val userId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListViewModel(repository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}