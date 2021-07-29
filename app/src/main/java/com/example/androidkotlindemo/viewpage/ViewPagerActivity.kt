package com.example.androidkotlindemo.viewpage

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.androidkotlindemo.R
import com.example.androidkotlindemo.common.KLog
import com.example.androidkotlindemo.mvvm.ViewModelFactory

class ViewPagerActivity : AppCompatActivity() {

    private var oldIndex = 0
    private lateinit var buttons: Array<Button>
    private val ids = arrayOf(R.id.bt_tab1, R.id.bt_tab2, R.id.bt_tab3, R.id.bt_tab4)

    private lateinit var viewModel: FragmentViewModel

    companion object {
        const val WHAT = "ViewPagerActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_pager_activity)

        initView()
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory.INSTANCE).get(FragmentViewModel::class.java)
//        viewModel.liveData.observe(this, Observer {
//            KLog.d(it)
//            if (it.what == WHAT) {
//                KLog.d(it.obj)
//            }
//        })
    }

    private fun initView() {

        val viewPager = findViewById<ViewPager>(R.id.view_pager)
        val fragments = ArrayList<ViewPageFragment>()
        fragments.add(ViewPageFragment.newInstance("tab1"))
        fragments.add(ViewPageFragment.newInstance("tab2"))
        fragments.add(ViewPageFragment.newInstance("tab3"))
        fragments.add(ViewPageFragment.newInstance("tab4"))
        viewPager.adapter = FragmentAdapter(supportFragmentManager, fragments)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
//                KLog.d(state)
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//                KLog.d(position)
            }

            override fun onPageSelected(position: Int) {
                KLog.d(position)
                buttons[oldIndex].setTextColor(Color.BLACK)
                buttons[position].setTextColor(Color.RED)
                oldIndex = position
            }
        })

        buttons = Array(4) {
            findViewById<Button>(ids[it]).apply {
                setOnClickListener { _ ->
                    viewPager.currentItem = it
                    viewModel.liveData.value =
                        Event("ViewPageFragment_tab${it + 1}", "click item $it")
                }
            }
        }
        buttons[0].setTextColor(Color.RED)
    }

}