package com.example.myapplication.classes

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: UserDB)

    @Query("SELECT * FROM users ORDER BY id DESC")
    suspend fun getAllUsers(): List<UserDB>

    @Query("SELECT * FROM users WHERE name = :userName LIMIT 1")
    suspend fun getUserByName(userName: String): UserDB?

    // Сохранить результат
    @Insert
    suspend fun insertRecord(record: RecordDB)

    // Получить все результаты конкретного игрока по имени
    @Query("SELECT * FROM game_records WHERE userName = :name ORDER BY date DESC")
    suspend fun getRecordsByUserName(name: String): List<RecordDB>

    // Получить ВСЕ результаты
//    @Query("SELECT * FROM game_records ORDER BY score DESC")
//    suspend fun getAllRecords(): List<RecordDB>
    @Query("SELECT * FROM game_records")
    suspend fun getAllRecords(): List<RecordDB>

    @Query("SELECT * FROM users ORDER BY name COLLATE NOCASE ASC, id ASC LIMIT :limit OFFSET :offset")
    suspend fun getUsersPage(limit: Int, offset: Int): List<UserDB>



}

