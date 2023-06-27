package com.example.fullthrottle.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fullthrottle.data.entities.Like
import com.example.fullthrottle.data.entities.LikeBool
import com.example.fullthrottle.data.entities.Motorbike
import com.example.fullthrottle.data.entities.Post
import com.example.fullthrottle.data.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface PostsDAO {
    @Query("SELECT * FROM posts ORDER BY publish_date DESC")
    fun getPosts(): Flow<List<Post>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(post: Post)

    @Query("DELETE FROM posts WHERE postId=:postId")
    fun delete(postId: String)

    @Query("DELETE FROM posts")
    fun deleteAll()
}

@Dao
interface UsersDAO {
    @Query("SELECT * FROM users WHERE userId=:uid")
    fun getUserById(uid: String): Flow<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)
}

@Dao
interface MotorbikesDAO {
    @Query("SELECT * FROM motorbikes WHERE motorbikeId=:mid")
    fun getMotorbikeById(mid: String): Flow<Motorbike>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(motorbike: Motorbike)

    @Query("DELETE FROM motorbikes")
    fun deleteAll()
}

@Dao
interface LikesDAO {
    @Query("SELECT * FROM likes WHERE post_id=:postId")
    fun getLike(postId: String): Flow<LikeBool>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(like: LikeBool)

    @Query("DELETE FROM likes")
    fun deleteAll()
}