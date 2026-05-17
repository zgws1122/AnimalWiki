package com.example.code.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.code.App
import com.example.code.data.local.entity.UserEntity
import com.example.code.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository = (application as App).userRepository
    private val prefs = application.getSharedPreferences("auth", 0)

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser

    val isLoggedIn: Boolean get() = _currentUser.value != null

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    private val _registerError = MutableStateFlow<String?>(null)
    val registerError: StateFlow<String?> = _registerError

    init {
        checkAutoLogin()
    }

    private fun checkAutoLogin() {
        val userId = prefs.getInt("logged_in_user_id", -1)
        if (userId == -1) return
        viewModelScope.launch {
            val user = repository.getUserById(userId)
            if (user != null) {
                _currentUser.value = user
            } else {
                prefs.edit().remove("logged_in_user_id").apply()
            }
        }
    }

    fun login(username: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _loginError.value = null
            repository.login(username, password)
                .onSuccess { user ->
                    _currentUser.value = user
                    prefs.edit().putInt("logged_in_user_id", user.id).apply()
                    onSuccess()
                }
                .onFailure { e ->
                    _loginError.value = e.message
                }
        }
    }

    fun register(username: String, password: String, nickname: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _registerError.value = null
            repository.register(username, password, nickname)
                .onSuccess { user ->
                    _currentUser.value = user
                    prefs.edit().putInt("logged_in_user_id", user.id).apply()
                    onSuccess()
                }
                .onFailure { e ->
                    _registerError.value = e.message
                }
        }
    }

    fun logout() {
        _currentUser.value = null
        prefs.edit().remove("logged_in_user_id").apply()
    }

    fun clearErrors() {
        _loginError.value = null
        _registerError.value = null
    }

    fun updateProfile(nickname: String, avatar: String?) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val updated = user.copy(nickname = nickname, avatar = avatar)
            repository.updateProfile(updated)
                .onSuccess { _currentUser.value = it }
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return AuthViewModel(application) as T
        }
    }
}
