package com.example.androidkotlindemo.viewpage

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.androidkotlindemo.R
import com.example.androidkotlindemo.common.KLog
import com.example.androidkotlindemo.mvvm.ViewModelFactory

class ViewPageFragment : Fragment() {

    companion object {
        fun newInstance(name: String): ViewPageFragment {
            val fragment = ViewPageFragment()
            val args = Bundle().apply {
                putString("name", name)
            }
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var viewModel: FragmentViewModel
    private lateinit var mName: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val name = arguments?.getString("name")
        if (name != null) mName = name
        KLog.d("$name")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KLog.d(mName)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        KLog.d(mName)
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        KLog.d(mName)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        KLog.d(mName)
        initViewModel()
        initView()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            activity!!,
            ViewModelFactory.INSTANCE
        ).get(FragmentViewModel::class.java)
        viewModel.liveData.observe(viewLifecycleOwner, Observer {
            KLog.d("$mName $it")
            if (it.what == "ViewPageFragment_$mName")
                KLog.d(it.obj)
        })
    }

    private fun initView() {
        val tv = view?.findViewById<TextView>(R.id.message)
        tv?.text = mName
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        val name = arguments?.getString("name")
        KLog.d("$name $isVisibleToUser $userVisibleHint")
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        KLog.d("$mName $hidden")
    }

    override fun onStart() {
        super.onStart()
        KLog.d(mName)
    }

    override fun onResume() {
        super.onResume()
        KLog.d(mName)
        viewModel.liveData.value = Event(ViewPagerActivity.WHAT, "$mName resume")
    }

    override fun onPause() {
        super.onPause()
        KLog.d(mName)
    }

    override fun onStop() {
        super.onStop()
        KLog.d(mName)
    }

    override fun onDestroy() {
        super.onDestroy()
        KLog.d(mName)
    }

    override fun onDetach() {
        super.onDetach()
        KLog.d(mName)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        KLog.d(mName)
    }
}