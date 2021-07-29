package com.example.androidkotlindemo.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by zxf on 2021/6/8
 */
@Entity(tableName = "tab_student")
data class Student(var name: String, var age: Int) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    override fun toString(): String {
        return "Student(name='$name', age=$age, id=$id)"
    }


}
