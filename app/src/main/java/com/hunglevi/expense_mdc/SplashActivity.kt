package com.hunglevi.expense_mdc

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.hunglevi.expense_mdc.databinding.ActivitySplashScreenBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply animation to the logo
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        binding.logoImage.startAnimation(fadeInAnimation)

        // Apply animation to app name and description
        val slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        binding.appName.startAnimation(slideUpAnimation)
        binding.sourceDesign.startAnimation(slideUpAnimation)

        // Delay for 3 seconds before navigating to LoginActivity
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close SplashActivity so the user can't return to it
        }, 3000) // Animation duration + delay time
    }
}