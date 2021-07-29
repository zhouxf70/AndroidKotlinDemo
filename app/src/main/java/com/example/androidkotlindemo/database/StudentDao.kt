package com.example.androidkotlindemo.database

import androidx.room.*

/**
 * Created by zxf on 2021/6/8
 */
@Dao
interface StudentDao {

    @Query("select * from tab_student")
    fun findAll(): List<Student>

    @Query("select * from tab_student where id = :id")
    fun findById(id: Long): Student?

    @Query("select * from tab_student where name = :name")
    fun findByName(name: String): Student

    @Insert
    fun insert(student: Student)

    @Delete
    fun delete(student: Student)

    @Query("delete from tab_student")
    fun deleteAll()

    @Query("delete  from tab_student where id=:id")
    fun deleteById(id: Long)

    @Update
    fun update(student: Student)
}