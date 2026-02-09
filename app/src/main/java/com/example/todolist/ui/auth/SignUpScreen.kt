package com.example.todolist.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
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

// --- DEFINIÇÃO DAS CORES LOCAIS (Para não dar erro de referência) ---
private val LightBlue = Color(0xFF4FC3F7)
private val Blue = Color(0xFF2196F3)
private val Purple = Color(0xFF7E57C2)
private val White = Color(0xFFFFFFFF)
private val BgGray = Color(0xFFF5F5F5)

@Composable
fun SignUpScreen(
    authRepository: AuthRepository,
    onSignUpSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(authRepository)
    )
    val context = LocalContext.current
    val isLoading = viewModel.isLoading

    val passwordError = viewModel.passwordError
    val emailError = viewModel.emailError

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is AuthEvent.Success -> {
                    Toast.makeText(context, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                    onSignUpSuccess()
                }
                is AuthEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGray)
    ) {
        // --- Elementos Decorativos ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = LightBlue,
                radius = 400f,
                center = Offset(x = size.width * 0.8f, y = size.height * 0.05f)
            )
            drawCircle(
                color = Purple,
                radius = 300f,
                center = Offset(x = size.width * 0.1f, y = size.height * 0.15f)
            )
        }

        // --- Cartão de Cadastro ---
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(32.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Ícone
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(LightBlue.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = "Ícone Cadastro",
                        tint = Blue,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Crie sua conta",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Campo de Email
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

                    // --- MUDANÇA: APENAS A COR DO TEXTO PARA PRETO ---
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = Blue,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                        focusedLabelColor = Blue,
                        cursorColor = Blue
                    ),
                    enabled = !isLoading,
                    isError = emailError != null,
                    supportingText = {
                        if (emailError != null) {
                            Text(text = emailError, color = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo de Senha
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        viewModel.onPasswordChange(it)
                    },
                    label = { Text("Senha") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),

                    // --- MUDANÇA: APENAS A COR DO TEXTO PARA PRETO ---
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = Blue,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                        focusedLabelColor = Blue,
                        cursorColor = Blue
                    ),
                    enabled = !isLoading,
                    isError = passwordError != null,
                    supportingText = {
                        if (passwordError != null) {
                            Text(text = passwordError, color = MaterialTheme.colorScheme.error)
                        } else {
                            Text(
                                text = "Mínimo 8 chars: Maiúscula, minúscula e número.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (isLoading) {
                    CircularProgressIndicator(color = Blue)
                } else {
                    Button(
                        onClick = { viewModel.signUp(email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue)
                    ) {
                        Text(text = "CADASTRAR", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = onNavigateBack) {
                        Text(text = "Já tem uma conta? Faça Login", color = Blue)
                    }
                }
            }
        }
    }
}