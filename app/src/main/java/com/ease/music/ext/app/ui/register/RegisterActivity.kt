package com.ease.music.ext.app.ui.register

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ease.music.ext.app.R
import com.ease.music.ext.app.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //状态栏中的文字颜色和图标颜色，需要android系统6.0以上，而且目前只有一种可以修改（一种是深色，一种是浅色即白色）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //
        actionBar?.title = getString(R.string.ease_login_title)

        val username = binding.username
        val password = binding.password
        val rePassword = binding.rePassword
        val register = binding.register
        val loading = binding.loading

        registerViewModel = ViewModelProvider(this, RegisterViewModelFactory())[RegisterViewModel::class.java]

        registerViewModel.loginFormState.observe(this@RegisterActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            register.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        registerViewModel.registerResult.observe(this@RegisterActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })

        username.afterTextChanged {
            registerViewModel.registerDataChanged(
                username.text.toString(),
                password.text.toString(),
                rePassword.text.toString()
            )
        }
        password.apply {
            afterTextChanged {
                registerViewModel.registerDataChanged(
                    username.text.toString(),
                    password.text.toString(),
                    rePassword.text.toString()
                )
            }

        }
        rePassword.apply {
            afterTextChanged {
                registerViewModel.registerDataChanged(
                    username.text.toString(),
                    password.text.toString(),
                    rePassword.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        registerViewModel.register(
                            username.text.toString(),
                            password.text.toString(),
                        )
                }
                false
            }

            register.setOnClickListener {
                loading.visibility = View.VISIBLE
                registerViewModel.register(username.text.toString(), password.text.toString())
            }

        }
    }

    private fun updateUiWithUser(model: RegisterUserView) {
        val registerSuccess = getString(R.string.register_success)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$registerSuccess $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}