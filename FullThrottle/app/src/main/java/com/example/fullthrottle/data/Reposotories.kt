package com.example.fullthrottle.data

import androidx.annotation.WorkerThread
import com.example.fullthrottle.data.entities.LikeBool
import com.example.fullthrottle.data.entities.Motorbike
import com.example.fullthrottle.data.entities.Post
import com.example.fullthrottle.data.entities.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class PostsRepository(private val postsDAO: PostsDAO) {

    val posts: Flow<List<Post>> = postsDAO.getPosts()

    @WorkerThread
    suspend fun insertNewPost(post: Post) {
        postsDAO.insert(post)
    }

    fun deletePost(postId: String) {
        postsDAO.delete(postId)
    }

    fun deleteAllPosts() {
        postsDAO.deleteAll()
    }
}

class UsersRepository(private val usersDAO: UsersDAO) {

    suspend fun getUserById(uid: String): User {
        return usersDAO.getUserById(uid).first()
    }

    @WorkerThread
    suspend fun insertNewUser(user: User) {
        usersDAO.insert(user)
    }
}

class MotorbikesRepository(private val motorbikesDAO: MotorbikesDAO) {

    @WorkerThread
    suspend fun insertNewMotorbike(motorbike: Motorbike) {
        motorbikesDAO.insert(motorbike)
    }

    suspend fun getMotorbikeById(mid: String): Motorbike {
        return motorbikesDAO.getMotorbikeById(mid).first()
    }

    fun deleteAllMotorbikes() {
        motorbikesDAO.deleteAll()
    }
}

class LikesRepository(private val likesDAO: LikesDAO) {

    @WorkerThread
    suspend fun insertNewLike(like: LikeBool) {
        likesDAO.insert(like)
    }

    suspend fun getLike(postId: String): Boolean {
        val like = likesDAO.getLike(postId).first()
        return like.value == true
    }

    fun deleteAllLikes() {
        likesDAO.deleteAll()
    }
}