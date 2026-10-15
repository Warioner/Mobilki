package com.example.myapplication.classes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_records")
data class RecordDB(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userName: String,
    val difficulty: Int,
    val score: Int,
    val duration: Int,
    val date: Long = System.currentTimeMillis()
)