package com.example.mborzenkov.githubusers.networking

import com.example.mborzenkov.githubusers.adt.UserItemJsonAdapter
import com.example.mborzenkov.githubusers.networking.dagger.GithubApiComponent
import com.squareup.moshi.Moshi
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject

/** Реализация подключения к API Github. */
class GithubApiImpl(component: GithubApiComponent) {

    companion object {
        /** Количество элемнетов на страницу. */
        const val PER_PAGE = 30
    }

    /** URL сервера для подключения. */
    @Inject
    lateinit var mBaseUrl: HttpUrl
    /** Интерфейс для подключения. */
    private val githubApi: GithubApi

    init {

        component.inject(this)

        // Логирование
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        // Адаптер JSON
        val moshi = Moshi.Builder().add(UserItemJsonAdapter()).build()

        // Создание подключения
        val retrofit = Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
        githubApi = retrofit.create(GithubApi::class.java)

    }

    /** Выполняет поиск пользователей.
     * См. [GithubApi.searchUsers]
     */
    fun searchUsers(query: String,
                    sort: String? = null,
                    order: String? = null,
                    page: Int? = null)
            = githubApi.searchUsers(query, sort, order, page, PER_PAGE)

    /** Выполняет поиск конкретного пользователя.
     * См. [GithubApi.getSingleUser]
     */
    fun getSingleUser(username: String) = githubApi.getSingleUser(username)

    /** Выполняет поиск топовых пользователей.
     * См. [GithubApi.getTopUsers]
     */
    fun getTopUsers(page: Int?) = githubApi.getTopUsers(page, PER_PAGE)

}