package com.example.spotechnify.authentication

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    // Shared state for authentication flow
    private val _authState = mutableStateOf(AuthState())
    val authState: State<AuthState> = _authState

    // Check if user is already authenticated
    fun checkAuthState() {
        _authState.value = _authState.value.copy(
            isAuthenticated = repository.hasAccessToken()
        )
    }

    // Clear authentication data (for logout)
    fun logout() {
        viewModelScope.launch {
            repository.clearAuthData()
            _authState.value = AuthState(
                isAuthenticated = false,
                isLoading = false,
                error = null
            )
        }
    }

    // Clear any authentication errors
    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }

    // Reset the entire auth state
    fun resetAuthState() {
        _authState.value = AuthState()
    }
}

data class AuthState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)