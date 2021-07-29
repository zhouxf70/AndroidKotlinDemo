package com.example.androidkotlindemo.paging

import android.animation.ObjectAnimator
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.androidkotlindemo.R
import com.example.androidkotlindemo.mvvm.ViewModelFactory
import kotlinx.android.synthetic.main.activity_paging.*

class PagingActivity : AppCompatActivity() {

    private val viewModel: PagerViewModel
        get() = ViewModelProvider(this, ViewModelFactory.INSTANCE).get(PagerViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paging)
        et_search.background
//        val loadAnimation = AnimationUtils.loadAnimation()
//        et_search.startAnimation(loadAnimation)
        ObjectAnimator.ofFloat(et_search, "alpha", 1f, 0.8f, 0.7f, 0.2f, 0.1f).start()
        et_search.scaleX = 1.2f
    }

}