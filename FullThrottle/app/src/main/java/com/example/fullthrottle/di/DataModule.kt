package com.example.fullthrottle.di

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fullthrottle.FullThrottle
import com.example.fullthrottle.data.PostsRepository
import com.example.fullthrottle.data.SettingsRepository
import com.example.fullthrottle.data.UsersRepository
import com.example.fullthrottle.viewModel.SettingsViewModel
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
}