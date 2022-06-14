package com.ease.music.ext.app.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ease.music.ext.app.R
import com.ease.music.ext.app.databinding.ActivitySplashBinding
import com.ease.music.ext.app.ui.login.LoginActivity
import kotlinx.coroutines.delay

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.translate_no, R.anim.translate_fade_out)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launchWhenResumed {
            delay(1000)
            finish()
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
        }
    }
}