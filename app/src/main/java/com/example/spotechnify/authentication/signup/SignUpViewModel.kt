package com.example.spotechnify.authentication.signup

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = mutableStateOf(SignUpState())
    val state: State<SignUpState> = _state

    fun onUsernameChange(username: String) {
        _state.value = _state.value.copy(username = username)
    }

    fun onEmailChange(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _state.value = _state.value.copy(password = password)
    }

    fun signUp() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            val result = repository.signUp(
                _state.value.username,
                _state.value.email,
                _state.value.password
            )

            _state.value = _state.value.copy(isLoading = false)

            result.fold(
                onSuccess = {
                    _state.value = _state.value.copy(isSignUpSuccessful = true)
                },
                onFailure = {
                    _state.value = _state.value.copy(
                        error = it.message ?: "Sign up failed"
                    )
                }
            )
        }
    }
}

data class SignUpState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSignUpSuccessful: Boolean = false
)