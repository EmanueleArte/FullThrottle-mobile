package com.example.fullthrottle.data

import androidx.annotation.WorkerThread
import com.example.fullthrottle.data.entities.*
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
        if (like == null) return false
        return like.value == true
    }

    fun deleteAllLikes() {
        likesDAO.deleteAll()
    }
}

class CommentsNotificationsRepository(private val commentsNotificationsDAO: CommentsNotificationsDAO) {

    val commentsNotifications: Flow<List<CommentNotification>> = commentsNotificationsDAO.getCommentsNotifications()

    @WorkerThread
    suspend fun insertNewCommentNotification(commentNotification: CommentNotification) {
        commentsNotificationsDAO.insert(commentNotification)
    }
}

class LikesNotificationsRepository(private val likesNotificationsDAO: LikesNotificationsDAO) {

    val likesNotifications: Flow<List<LikeNotification>> = likesNotificationsDAO.getLikesNotifications()

    @WorkerThread
    suspend fun insertNewLikeNotification(likeNotification: LikeNotification) {
        likesNotificationsDAO.insert(likeNotification)
    }
}

class FollowsNotificationsRepository(private val followsNotificationsDAO: FollowsNotificationsDAO) {

    val followsNotifications: Flow<List<FollowNotification>> = followsNotificationsDAO.getFollowsNotifications()

    @WorkerThread
    suspend fun insertNewFollowNotification(followNotification: FollowNotification) {
        followsNotificationsDAO.insert(followNotification)
    }
}
