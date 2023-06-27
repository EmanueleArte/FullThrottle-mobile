package com.example.fullthrottle.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullthrottle.data.entities.Like
import com.example.fullthrottle.data.entities.LikeBool
import com.example.fullthrottle.data.entities.Motorbike
import com.example.fullthrottle.data.entities.Post
import com.example.fullthrottle.data.entities.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalDbViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    private val usersRepository: UsersRepository,
    private val motorbikesRepository: MotorbikesRepository,
    private val likesRepository: LikesRepository
) : ViewModel() {
    val posts = postsRepository.posts

    fun addNewPost(post: Post) = viewModelScope.launch {
        postsRepository.insertNewPost(post)
    }

    fun deletePost(postId: String) {
        postsRepository.deletePost(postId)
    }

    fun deleteAllPosts() {
        postsRepository.deleteAllPosts()
    }

    fun addNewUser(user: User) = viewModelScope.launch {
        usersRepository.insertNewUser(user)
    }

    suspend fun getUserById(uid: String): User {
        return usersRepository.getUserById(uid)
    }

    fun addNewMotorbike(motorbike: Motorbike) = viewModelScope.launch {
        motorbikesRepository.insertNewMotorbike(motorbike)
    }

    suspend fun getMotorbikeById(mid: String): Motorbike {
        return motorbikesRepository.getMotorbikeById(mid)
    }

    fun deleteAllMotorbikes() {
        motorbikesRepository.deleteAllMotorbikes()
    }

    fun addNewLike(like: LikeBool) = viewModelScope.launch {
        likesRepository.insertNewLike(like)
    }

    suspend fun getLike(postId: String): Boolean {
        return likesRepository.getLike(postId)
    }

    fun deleteAllLikes() {
        likesRepository.deleteAllLikes()
    }
}