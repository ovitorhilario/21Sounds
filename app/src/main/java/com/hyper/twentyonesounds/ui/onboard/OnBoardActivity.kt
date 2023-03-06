package com.hyper.twentyonesounds.ui.onboard

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.lifecycleScope
import com.hyper.twentyonesounds.databinding.ActivityOnboardBinding
import com.hyper.twentyonesounds.ui.main.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OnBoardActivity : AppCompatActivity() {

    val binding by lazy { ActivityOnboardBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // window.statusBarColor = ContextCompat.getColor(this, R.color.blue_high)

        lifecycleScope.launch {
            supportFragmentManager.
                commit {
                    setCustomAnimations(androidx.transition.R.anim.abc_fade_in, androidx.transition.R.anim.abc_fade_out)
                    setReorderingAllowed(true)
                    replace<OnBoardLogo>(binding.fcvOnboard.id)
                }

                delay(2000L)

                val intent = Intent(this@OnBoardActivity, MainActivity::class.java)
                this@OnBoardActivity.finish()
                startActivity(intent)
        }

    }

}