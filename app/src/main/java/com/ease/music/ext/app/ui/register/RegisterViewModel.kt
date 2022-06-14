package com.ease.music.ext.app.ui.register

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ease.music.ext.app.R
import com.ease.music.ext.app.data.LoginRepository
import com.ease.music.ext.app.data.Result

class RegisterViewModel(private val registerRepository: LoginRepository) : ViewModel() {

    private val _registerForm = MutableLiveData<RegisterFormState>()
    val loginFormState: LiveData<RegisterFormState> = _registerForm

    private val _registerResult = MutableLiveData<RegisterResult>()
    val registerResult: LiveData<RegisterResult> = _registerResult

    /**
     * 注册
     */
    fun register(username: String, password: String) {
        // can be launched in a separate asynchronous job
        val result = registerRepository.login(username, password)

        if (result is Result.Success) {
            _registerResult.value = RegisterResult(success = RegisterUserView(displayName = result.data.displayName))
        } else {
            _registerResult.value = RegisterResult(error = R.string.login_failed)
        }
    }

    fun registerDataChanged(username: String, password: String, rePassword: String) {
        if (!isUserNameValid(username)) {
            _registerForm.value = RegisterFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _registerForm.value = RegisterFormState(passwordError = R.string.invalid_password)
        } else if (password != rePassword) {
            _registerForm.value = RegisterFormState(passwordError = R.string.invalid_re_password)
        } else {
            _registerForm.value = RegisterFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}