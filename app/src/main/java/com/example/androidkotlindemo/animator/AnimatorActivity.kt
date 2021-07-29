package com.example.androidkotlindemo.animator

import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.androidkotlindemo.R
import kotlinx.android.synthetic.main.activity_animator.*

class AnimatorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animator)

        val ofFloat = ObjectAnimator.ofFloat(tv_animator, "alpha", 1.0f, 0f)
        ofFloat.duration = 2000
        tv_animator.setOnClickListener {
            ofFloat.start()
        }
    }
}