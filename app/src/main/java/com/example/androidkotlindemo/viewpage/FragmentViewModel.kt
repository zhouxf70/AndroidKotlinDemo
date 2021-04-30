package com.example.androidkotlindemo.viewpage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FragmentViewModel : ViewModel() {

    val liveData = MutableLiveData<Event>()
}