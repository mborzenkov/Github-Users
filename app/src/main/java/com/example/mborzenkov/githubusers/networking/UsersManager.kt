package com.example.mborzenkov.githubusers.networking

import com.example.mborzenkov.githubusers.adt.UserItem
import io.reactivex.Single

/** Класс для управления получением данных пользователей из сети. */
class UsersManager(private val api: GithubApiImpl) {

    /** Выполняет поиск пользовваателей.
     *
     * @param query текст запроса
     * @param page запрашиваемая страница
     *
     * @return Single, оповещающий об успешном выполнении или ошибке
     */
    fun searchUsers(query: String, page: Int): Single<GithubApi.UsersResponse> {
        return Single.create { subscriber ->
            val searchResponse = api.searchUsers(query, null, null, page).execute()
            if (searchResponse.isSuccessful)
                subscriber.onSuccess(searchResponse.body()!!)
            else
                subscriber.onError(Throwable(searchResponse.message()))
        }
    }

    /** Выполняет поиск топовых пользователей.
     *
     * @param page запрашиваемая страница
     *
     * @return Single, оповещающий об успешном выполнении или ошибке
     */
    fun topUsers(page: Int): Single<GithubApi.UsersResponse> {
        return Single.create { subscriber ->
            val topUsersResponse = api.getTopUsers(page).execute()
            if (topUsersResponse.isSuccessful)
                subscriber.onSuccess(topUsersResponse.body()!!)
            else
                subscriber.onError(Throwable(topUsersResponse.message()))
        }
    }

    /** Выполняет запрос на данные конкретного пользователя.
     *
     * @param username имя пользователя
     *
     * @return Single, оповещающий об успешном выполнении или ошибке
     */
    fun getSingleUser(username: String): Single<UserItem> {
        return Single.create {
            subscriber ->
            val singleUserResponse = api.getSingleUser(username).execute()
            if (singleUserResponse.isSuccessful)
                subscriber.onSuccess(singleUserResponse.body()!!)
            else
                subscriber.onError(Throwable(singleUserResponse.message()))
        }
    }

}
