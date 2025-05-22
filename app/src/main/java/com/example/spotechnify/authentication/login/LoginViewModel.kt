package com.example.spotechnify.authentication.login

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = mutableStateOf(LoginState())
    val state: State<LoginState> = _state

    fun onUsernameChange(username: String) {
        _state.value = _state.value.copy(username = username)
    }

    fun onPasswordChange(password: String) {
        _state.value = _state.value.copy(password = password)
    }

    fun login() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            val result = repository.login(
                _state.value.username,
                _state.value.password
            )

            _state.value = _state.value.copy(isLoading = false)

            result.fold(
                onSuccess = {
                    _state.value = _state.value.copy(isLoginSuccessful = true)
                },
                onFailure = {
                    _state.value = _state.value.copy(
                        error = it.message ?: "Login failed"
                    )
                }
            )
        }
    }
}

data class LoginState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoginSuccessful: Boolean = false
)