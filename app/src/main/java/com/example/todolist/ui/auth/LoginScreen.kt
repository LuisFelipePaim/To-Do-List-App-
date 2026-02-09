package com.example.todolist.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolist.data.AuthRepository

// --- DEFINIÇÃO DAS CORES LOCAIS (Para evitar erros de referência) ---
private val LoginLightBlue = Color(0xFF4FC3F7)
private val LoginBlue = Color(0xFF2196F3)
private val LoginPurple = Color(0xFF7E57C2)
private val LoginWhite = Color(0xFFFFFFFF)
private val LoginBgGray = Color(0xFFF5F5F5)

@Composable
fun LoginScreen(
    authRepository: AuthRepository,
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(authRepository)
    )

    val context = LocalContext.current

    // Variáveis de estado do ViewModel
    val isLoading = viewModel.isLoading
    val emailError = viewModel.emailError

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Observa eventos (Sucesso ou Erro)
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is AuthEvent.Success -> {
                    onLoginSuccess()
                }
                is AuthEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LoginBgGray)
    ) {
        // --- 1. Elementos Decorativos (Bolinhas) ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = LoginLightBlue,
                radius = 400f,
                center = Offset(x = size.width * 0.2f, y = size.height * 0.05f)
            )
            drawCircle(
                color = LoginPurple,
                radius = 300f,
                center = Offset(x = size.width * 0.9f, y = size.height * 0.15f)
            )
        }

        // --- 2. Cartão de Login ---
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(32.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = LoginWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Ícone de Perfil
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(LoginLightBlue.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Ícone Login",
                        tint = LoginBlue,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Bem-vindo",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // --- CAMPO DE EMAIL ---
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        viewModel.onEmailChange(it)
                    },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,

                    // AQUI A CORREÇÃO (TEXTO PRETO)
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = LoginBlue,
                        focusedBorderColor = LoginBlue,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                        focusedLabelColor = LoginBlue,
                        unfocusedLabelColor = Color.Gray
                    ),
                    isError = emailError != null,
                    enabled = !isLoading,
                    supportingText = {
                        if (emailError != null) Text(text = emailError, color = MaterialTheme.colorScheme.error)
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // --- CAMPO DE SENHA ---
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Senha") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,

                    // AQUI A CORREÇÃO (TEXTO PRETO)
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = LoginBlue,
                        focusedBorderColor = LoginBlue,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                        focusedLabelColor = LoginBlue,
                        unfocusedLabelColor = Color.Gray
                    ),
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(24.dp))

                // BOTÃO DE LOGIN
                if (isLoading) {
                    CircularProgressIndicator(color = LoginBlue)
                } else {
                    Button(
                        onClick = { viewModel.login(email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LoginBlue)
                    ) {
                        Text(text = "ENTRAR", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // LINK CADASTRO
                    TextButton(onClick = onNavigateToSignUp) {
                        Text(text = "Não tem conta? Cadastre-se", color = LoginBlue)
                    }
                }
            }
        }
    }
}