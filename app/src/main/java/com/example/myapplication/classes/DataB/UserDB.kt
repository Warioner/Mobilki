package com.example.myapplication.classes

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["name"], unique = true)]
)
data class UserDB(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val course: Int,
    val difficulty: Int,
    val birthDay: Int,
    val birthMonth: Int,
    val birthYear: Int,
    val gender: String,
    val zodiac: String
)