package com.example.fullthrottle.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fullthrottle.data.entities.Post
import com.example.fullthrottle.data.entities.User

@Database(entities = [Post::class, User::class], version = 1, exportSchema = true)
abstract class LocalDB : RoomDatabase() {

    abstract fun postsDAO(): PostsDAO
    abstract fun usersDAO(): UsersDAO

    companion object {
        @Volatile
        private var INSTANCE: LocalDB ?= null

        fun getDatabase(context: Context): LocalDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalDB::class.java,
                    "items_database"
                ).build()
                INSTANCE = instance

                instance
            }
        }
    }

}