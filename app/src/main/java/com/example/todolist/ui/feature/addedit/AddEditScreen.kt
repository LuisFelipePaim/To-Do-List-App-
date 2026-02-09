package com.example.todolist.ui.feature.addedit

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolist.data.TodoRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    todoRepository: TodoRepository,
    todoId: String?,
    navigateBack: () -> Unit
) {
    val viewModel: AddEditViewModel = viewModel(
        factory = AddEditViewModelFactory(todoRepository, todoId)
    )

    val title = viewModel.title
    val description = viewModel.description
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.PopBackStack -> navigateBack()
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(AddEditEvent.OnSaveTodo) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Salvar")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // CAMPO TÍTULO
            OutlinedTextField(
                value = title,
                onValueChange = { viewModel.onEvent(AddEditEvent.OnTitleChange(it)) },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                /** Gemini - início
                 * Prompt: Refatorar as cores do componente OutlinedTextField para corrigir problemas de acessibilidade.
                 * O texto de entrada apresenta baixo contraste com o fundo atual.
                 * Solicito a definição explícita da cor do texto para preto (Color.Black) nos estados focado e desfocado.
                 */
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = Color.Black
                )
                /** Gemini - final */
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CAMPO DESCRIÇÃO
            OutlinedTextField(
                value = description,
                onValueChange = { viewModel.onEvent(AddEditEvent.OnDescriptionChange(it)) },
                label = { Text("Descrição") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                maxLines = 10,
                /** Gemini - início
                 * Prompt: Aplicar as mesmas correções de estilo utilizadas no campo de Título.
                 * Forçar a cor da fonte para preto (Color.Black) através do parâmetro 'colors'
                 * para garantir a legibilidade do texto descritivo.
                 */
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = Color.Black
                )
                /** Gemini - final */
            )
        }
    }
}