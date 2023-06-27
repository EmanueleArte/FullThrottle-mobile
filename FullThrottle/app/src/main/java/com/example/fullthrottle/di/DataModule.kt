package com.example.fullthrottle.di

import android.content.Context
import com.example.fullthrottle.FullThrottle
import com.example.fullthrottle.data.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Singleton
    @Provides
    fun provideSettingsRepository(@ApplicationContext context: Context) = SettingsRepository(context)

    @Singleton
    @Provides
    fun providePostsRepository(@ApplicationContext context: Context) =
        PostsRepository((context.applicationContext as FullThrottle).database.postsDAO())

    @Singleton
    @Provides
    fun provideUsersRepository(@ApplicationContext context: Context) =
        UsersRepository((context.applicationContext as FullThrottle).database.usersDAO())

    @Singleton
    @Provides
    fun provideMotorbikesRepository(@ApplicationContext context: Context) =
        MotorbikesRepository((context.applicationContext as FullThrottle).database.motorbikesDAO())

    @Singleton
    @Provides
    fun provideLikesRepository(@ApplicationContext context: Context) =
        LikesRepository((context.applicationContext as FullThrottle).database.likesDAO())

    @Singleton
    @Provides
    fun provideCommentsNotificationsRepository(@ApplicationContext context: Context) =
        CommentsNotificationsRepository((context.applicationContext as FullThrottle).database.commentsNotificationsDAO())

    @Singleton
    @Provides
    fun provideLikesNotificationsRepository(@ApplicationContext context: Context) =
        LikesNotificationsRepository((context.applicationContext as FullThrottle).database.likesNotificationsDAO())

    @Singleton
    @Provides
    fun provideFollowsNotificationsRepository(@ApplicationContext context: Context) =
        FollowsNotificationsRepository((context.applicationContext as FullThrottle).database.followsNotificationsDAO())

}