package com.example.todolist.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.todolist.data.AuthRepository
import com.example.todolist.data.TodoRepository
import com.example.todolist.ui.auth.LoginScreen
import com.example.todolist.ui.auth.SignUpScreen
import com.example.todolist.ui.feature.addedit.AddEditScreen
import com.example.todolist.ui.feature.list.ListScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun TodoNavHost(
    navController: NavHostController,
    authRepository: AuthRepository,
    todoRepository: TodoRepository
) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // TELA DE LOGIN
        composable("login") {
            LoginScreen(
                authRepository = authRepository,
                onLoginSuccess = {
                    navController.navigate("list") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate("signup")
                }
            )
        }

        // TELA DE CADASTRO
        composable("signup") {
            SignUpScreen(
                authRepository = authRepository,
                onSignUpSuccess = {
                    navController.navigate("list") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // TELA DE LISTA
        composable("list") {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            ListScreen(
                repository = todoRepository,
                userId = userId,
                onNavigateToAdd = {
                    navController.navigate("add_edit")
                },
                onNavigateToEdit = { todoId ->
                    navController.navigate("add_edit?todoId=$todoId")
                },
                onLogout = {
                    authRepository.logout()
                    navController.navigate("login") {
                        popUpTo("list") { inclusive = true }
                    }
                }
            )
        }

        // TELA DE ADICIONAR/EDITAR
        composable(
            route = "add_edit?todoId={todoId}",
            arguments = listOf(
                navArgument("todoId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val todoId = backStackEntry.arguments?.getString("todoId")

            AddEditScreen(
                todoRepository = todoRepository,
                todoId = todoId,
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}