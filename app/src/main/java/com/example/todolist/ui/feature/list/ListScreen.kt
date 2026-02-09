package com.example.todolist.ui.feature.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolist.data.TodoRepository
import com.example.todolist.ui.components.TodoItem

val BgGray = Color(0xFFF5F5F5)
val MainBlue = Color(0xFF2196F3)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    repository: TodoRepository,
    userId: String,
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onLogout: () -> Unit
) {
    val viewModel: ListViewModel = viewModel(
        factory = ListViewModelFactory(repository, userId)
    )
    val todos by viewModel.todos.collectAsState()

    // --- CORREÇÃO IMPORTANTE: RECARREGAR AO VOLTAR ---
    val lifecycleOwner = LocalLifecycleOwner.current

    /** Gemini - início
     * Prompt: Implementar um observador de ciclo de vida (LifecycleObserver) para garantir a consistência dos dados.
     * O objetivo é interceptar o evento ON_RESUME para forçar o recarregamento da lista de tarefas,
     * assegurando que alterações feitas em outras telas sejam refletidas imediatamente ao retornar.
     */
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Sempre que a tela aparecer/voltar, recarrega a lista
                viewModel.loadTodos()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    /** Gemini - final */
    // ---------------------------------------------------

    Scaffold(
        containerColor = BgGray,
        topBar = {
            TopAppBar(
                title = { Text("Minhas Tarefas", fontWeight = FontWeight.Bold, color = Color.White) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sair", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MainBlue)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = MainBlue,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            if (todos.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Nenhuma tarefa por aqui...", color = Color.Gray, fontSize = 18.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
                ) {
                    items(todos) { todo ->
                        TodoItem(
                            todo = todo,
                            onCheckedChange = { isChecked ->
                                val updatedTodo = todo.copy(isCompleted = isChecked)
                                viewModel.toggleTodo(updatedTodo)
                            },
                            onDelete = { viewModel.deleteTodo(todo) },
                            onItemClick = { onNavigateToEdit(todo.id) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}