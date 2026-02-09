package com.example.todolist.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolist.domain.Todo

// Cores
val Blue = Color(0xFF2196F3)
val White = Color(0xFFFFFFFF)
val RedDelete = Color(0xFFE57373)

@Composable
fun TodoItem(
    todo: Todo,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Checkbox(
                // Verifica se o campo se chama 'isDone' ou 'isCompleted' no seu Todo.kt
                // Se der erro aqui, troque para todo.isCompleted
                checked = todo.isCompleted,
                onCheckedChange = { isChecked ->
                    onCheckedChange(isChecked)
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = Blue,
                    uncheckedColor = Color.Gray,
                    checkmarkColor = White
                )
            )

            // Texto (Clicável para editar)
            Text(
                text = todo.title,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
                    .clickable { onItemClick() }, // Clique no texto abre edição
                fontSize = 16.sp,
                color = if (todo.isCompleted) Color.Gray else Color.Black,
                textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else null,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Botão Deletar
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Deletar",
                    tint = RedDelete
                )
            }
        }
    }
}