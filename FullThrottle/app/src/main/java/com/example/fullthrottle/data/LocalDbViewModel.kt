package com.example.fullthrottle.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullthrottle.data.entities.Post
import com.example.fullthrottle.data.entities.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalDbViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    private val usersRepository: UsersRepository
) : ViewModel() {
    val posts = postsRepository.posts

    fun addNewPost(post: Post) = viewModelScope.launch {
        postsRepository.insertNewPost(post)
    }

    fun addNewUser(user: User) = viewModelScope.launch {
        usersRepository.insertNewUser(user)
    }

    suspend fun getUserById(uid: String): User {
        return usersRepository.getUserById(uid)
    }
}