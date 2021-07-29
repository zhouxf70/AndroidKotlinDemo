package com.example.androidkotlindemo.viewpage

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * Created by zxf on 2021/4/28
 */
class FragmentAdapter(fm: FragmentManager, private val fragments: List<ViewPageFragment>) :
    FragmentPagerAdapter(
        fm,
//        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        BEHAVIOR_SET_USER_VISIBLE_HINT
    ) {
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }
}