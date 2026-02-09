package com.example.todolist.data

import com.example.todolist.domain.Todo

interface TodoRepository {
    suspend fun getTodos(userId: String): Result<List<Todo>>
    suspend fun addTodo(todo: Todo): Result<Unit>
    suspend fun updateTodo(todo: Todo): Result<Unit> // <--- ESSA É A IMPORTANTE AGORA
    suspend fun deleteTodo(todoId: String): Result<Unit>
    suspend fun getTodoById(id: String): Result<Todo?>
}