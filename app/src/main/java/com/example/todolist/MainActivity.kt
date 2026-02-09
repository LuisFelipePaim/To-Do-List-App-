package com.example.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.todolist.data.AuthRepository
import com.example.todolist.data.FirestoreRepositoryImpl
import com.example.todolist.navigation.TodoNavHost
import com.example.todolist.ui.theme.TodoListTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        val authRepository = AuthRepository(auth)
        val todoRepository = FirestoreRepositoryImpl(db)

        setContent {
            // Usando TodoListTheme exatamente como está no seu arquivo de tema
            TodoListTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    TodoNavHost(
                        navController = navController,
                        authRepository = authRepository,
                        todoRepository = todoRepository
                    )
                }
            }
        }
    }
}