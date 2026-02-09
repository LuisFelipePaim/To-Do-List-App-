package com.example.todolist.data

import com.example.todolist.domain.Todo
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepositoryImpl(
    private val firestore: FirebaseFirestore
) : TodoRepository {

    private val collection = firestore.collection("todos")

    // Busca todas as tarefas do usuário e retorna como Resultado
    override suspend fun getTodos(userId: String): Result<List<Todo>> {
        return try {
            val snapshot = collection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            // Converte os documentos do Firestore para sua classe Todo
            val todos = snapshot.toObjects(Todo::class.java)
            Result.success(todos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Adiciona uma nova tarefa (gera ID automático se não tiver)
    override suspend fun addTodo(todo: Todo): Result<Unit> {
        return try {
            val newDocRef = collection.document()
            // Garante que o objeto salvo tenha o ID gerado pelo Firestore
            val newTodo = todo.copy(id = newDocRef.id)
            newDocRef.set(newTodo).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Atualiza uma tarefa existente (incluindo o status do checkbox)
    override suspend fun updateTodo(todo: Todo): Result<Unit> {
        return try {
            // Usa .set() para sobrescrever o documento com os novos dados (título, isDone, etc)
            collection.document(todo.id).set(todo).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Deleta uma tarefa
    override suspend fun deleteTodo(todoId: String): Result<Unit> {
        return try {
            collection.document(todoId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Busca uma tarefa específica (usado na tela de edição)
    override suspend fun getTodoById(id: String): Result<Todo?> {
        return try {
            val doc = collection.document(id).get().await()
            val todo = doc.toObject(Todo::class.java)
            Result.success(todo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}