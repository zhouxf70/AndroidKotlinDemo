package com.example.androidkotlindemo.database

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.androidkotlindemo.R
import com.example.androidkotlindemo.common.KLog
import kotlinx.android.synthetic.main.activity_room.*

class RoomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)


        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                KLog.d("Room : $event")
            }
        })

        AppDatabase.init(application)
        bt_insert.setOnClickListener { add(et_name.text.toString(), et_age.text.toString().toInt()) }
        bt_delete_by_id.setOnClickListener { delete(et_id.text.toString().toLong()) }
        bt_update.setOnClickListener {
            val findById = AppDatabase.instance.studentDao().findById(et_id.text.toString().toLong())
            if (findById == null) {
                KLog.d("can't find student by id=${et_id.text.toString().toLong()}")
                return@setOnClickListener
            }
            findById.name = et_name.text.toString()
            findById.age = et_age.text.toString().toInt()
            update(findById)
        }
        bt_find_all.setOnClickListener { findAll() }
        bt_find_by_name.setOnClickListener { findByName(et_name.text.toString()) }
        bt_delete_all.setOnClickListener { AppDatabase.instance.studentDao().deleteAll() }
    }

    private fun add(name: String, age: Int) {
        AppDatabase.instance.studentDao().insert(Student(name, age))
    }

    private fun delete(id: Long) {
        AppDatabase.instance.studentDao().deleteById(id)
    }

    private fun update(student: Student) {
        AppDatabase.instance.studentDao().update(student)
    }

    private fun findAll() {
        AppDatabase.instance.studentDao().findAll().forEach {
            KLog.d(it)
        }
    }

    private fun findByName(name: String) {
        AppDatabase.instance.studentDao().findByName(name).also { KLog.d(it) }
    }
}