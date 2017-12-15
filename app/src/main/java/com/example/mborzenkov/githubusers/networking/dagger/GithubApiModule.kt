package com.example.mborzenkov.githubusers.networking.dagger

import com.example.mborzenkov.githubusers.networking.GithubApi
import dagger.Module
import dagger.Provides
import okhttp3.HttpUrl
import javax.inject.Singleton

/** Модуль для иньекций, предоставляет url сервера.
 *
 * @param baseUrl url сервера
 */
@Module
class GithubApiModule(baseUrl: HttpUrl?) {

    private val mBaseUrl = if (baseUrl == null) GithubApi.BASE_URL else baseUrl

    /** Возвращает установленный url сервера. */
    @Provides
    @Singleton
    fun provideBaseUrl(): HttpUrl {
        return mBaseUrl
    }

}