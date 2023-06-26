package com.example.fullthrottle.data

import androidx.annotation.WorkerThread
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
