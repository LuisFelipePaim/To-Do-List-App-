package com.example.todolist.ui.auth

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    // Erros específicos para cada campo
    var passwordError by mutableStateOf<String?>(null)
        private set

    var emailError by mutableStateOf<String?>(null)
        private set

    private val _uiEvent = Channel<AuthEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    // Limpa erros ao digitar
    fun onPasswordChange(newPassword: String) {
        passwordError = null
    }

    fun onEmailChange(newEmail: String) {
        emailError = null
    }

    fun login(email: String, pass: String) {
        // Valida e-mail antes de enviar
        val eError = validateEmail(email)
        if (eError != null) {
            emailError = eError
            return
        }

        isLoading = true
        viewModelScope.launch {
            val result = repository.login(email.trim(), pass.trim())
            isLoading = false

            if (result.isSuccess) {
                _uiEvent.send(AuthEvent.Success)
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Erro desconhecido"
                _uiEvent.send(AuthEvent.Error(errorMsg))
            }
        }
    }

    /** Gemini - início
     * Prompt: Implementar a lógica de cadastro de usuários (SignUp) com validação robusta.
     * O fluxo deve incluir:
     * 1. Validação de formato de e-mail.
     * 2. Verificação de complexidade de senha (mínimo 8 caracteres, contendo letras maiúsculas, minúsculas, números e símbolos).
     * 3. Execução assíncrona da chamada ao repositório (Firebase) com gerenciamento de estado de carregamento (loading).
     * 4. Emissão de eventos de UI para sucesso ou tratamento de erros.
     */
    fun signUp(email: String, pass: String) {
        // 1. Valida E-mail
        val eError = validateEmail(email)
        if (eError != null) {
            emailError = eError
            return
        }

        // 2. Valida Senha
        val pError = validatePassword(pass)
        if (pError != null) {
            passwordError = pError
            return
        }

        // 3. Se tudo ok, chama o Firebase
        isLoading = true
        viewModelScope.launch {
            val result = repository.signUp(email.trim(), pass.trim())
            isLoading = false

            if (result.isSuccess) {
                _uiEvent.send(AuthEvent.Success)
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Erro desconhecido"
                _uiEvent.send(AuthEvent.Error(errorMsg))
            }
        }
    }
    /** Gemini - final */

    private fun validateEmail(email: String): String? {
        if (email.isBlank()) return "O e-mail não pode estar vazio."
        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            return "Digite um e-mail válido."
        }
        return null
    }

    private fun validatePassword(password: String): String? {
        if (password.length < 8) return "Mínimo de 8 caracteres."
        if (!password.any { it.isDigit() }) return "A senha deve conter pelo menos um número."
        if (!password.any { it.isUpperCase() }) return "A senha deve conter uma letra maiúscula."
        if (!password.any { it.isLowerCase() }) return "A senha deve conter uma letra minúscula."
        if (!password.any { !it.isLetterOrDigit() }) return "A senha deve conter um símbolo especial."
        return null
    }
}

sealed class AuthEvent {
    object Success : AuthEvent()
    data class Error(val message: String) : AuthEvent()
}

class AuthViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}