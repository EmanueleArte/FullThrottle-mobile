package com.example.fullthrottle.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullthrottle.data.entities.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalDbViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    private val usersRepository: UsersRepository,
    private val motorbikesRepository: MotorbikesRepository,
    private val likesRepository: LikesRepository,
    private val commentsNotificationsRepository: CommentsNotificationsRepository,
    private val likesNotificationsRepository: LikesNotificationsRepository,
    private val followsNotificationsRepository: FollowsNotificationsRepository
) : ViewModel() {
    val posts = postsRepository.posts

    val commentsNotifications = commentsNotificationsRepository.commentsNotifications
    val likesNotifications = likesNotificationsRepository.likesNotifications
    val followsNotifications = followsNotificationsRepository.followsNotifications

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

    // NOTIFICATIONS
    fun addNewCommentNotification(commentNotification: CommentNotification) = viewModelScope.launch {
        commentsNotificationsRepository.insertNewCommentNotification(commentNotification)
    }

    fun addNewLikeNotification(likeNotification: LikeNotification) = viewModelScope.launch {
        likesNotificationsRepository.insertNewLikeNotification(likeNotification)
    }

    fun addNewFollowNotification(followNotification: FollowNotification) = viewModelScope.launch {
        followsNotificationsRepository.insertNewFollowNotification(followNotification)
    }
}