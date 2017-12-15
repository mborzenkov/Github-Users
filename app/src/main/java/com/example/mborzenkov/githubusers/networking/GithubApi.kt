package com.example.mborzenkov.githubusers.networking

import android.support.annotation.StringDef
import com.example.mborzenkov.githubusers.adt.UserItem
import okhttp3.HttpUrl
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** Класс описывает API для доступа к пользователям GitHub.
 *  @see <a href="https://developer.github.com/v3/">GitHub API Doc</a>
 */
interface GithubApi {

    /* Документация API
    *
    * Доступ осуществляется по адресу https://api.github.com/
    *
    * В API присутствуют следующие методы:
    *      GET /search/users — выполняет поиск пользователей
    *
    * Успешный ответ отличается статусом 200 OK.
    *      Данные содержатся в корне ответа.
    * При ошибках 400, 422.. будет содержаться поле "message" с информацией об ошибке.
    */


    /////////////////////////
    // Константы

    companion object {

        /** Адрес для подключения.  */
        val BASE_URL: HttpUrl =
                HttpUrl.Builder().scheme("https").host("api.github.com").build()

        /** Параметр поисковый запрос. */
        const val PARAM_QUERY = "q"
        /** Параметр сортировка. */
        const val PARAM_SORT = "sort"
        /** Параметр порядок. */
        const val PARAM_ORDER = "order"
        /** Параметр текущая страница. */
        const val PARAM_PAGE = "page"
        /** Параметр результатов на странице. */
        const val PARAM_PER_PAGE = "per_page"
        /** Параметр имя пользователя. */
        const val PARAM_USERNAME = "username"

        /** Путь к поиску пользователей. */
        const val PATH_SEARCH_USERS = "search/users"
        /** Путь к поиску топовых пользователей. */
        const val PATH_SEARCH_TOP_USERS =
                "search/users?q=type:user+repos:%3E42+followers:%3E1000&sort=followers"
        /** Путь к запросу конкретного пользователя. */
        const val PATH_USER = "users/{$PARAM_USERNAME}"

        /** Возможные варианты сортировок. */
        @StringDef("followers", "repositories", "joined")
        annotation class Sort
        /** Возможные варианты порядка. */
        @StringDef("asc", "desc")
        annotation class Order

    }


    /////////////////////////
    // Ответы сервера

    /** Формат ответа сервера при запросе списка всех заметок.  */
    class UsersResponse {
        /** Общее число результатов в ответе. */
        var total_count: Int = 0
        /** Список пользоватлей.  */
        var items: List<UserItem>? = null
    }


    /////////////////////////
    // Доступные методы

    /** Выполняет поиск пользователей.
     *
     * @param query значение запроса, например "square"
     * @param sort вариант сортировки, один из @Sort, необязательный
     * @param order вариант порядка, asc или desc, необязательный
     * @param page запрашиваемая страница, необязательный (равно 1)
     * @param perPage число результатов на странице, необязательный (равно 30)
     *
     * @see <a href="https://developer.github.com/v3/search/#search-users">Search users API</a>
     * @see <a href="https://developer.github.com/v3/#pagination">Pagination</a>
     */
    @GET(PATH_SEARCH_USERS)
    fun searchUsers(@Query(PARAM_QUERY) query: String,
                    @Query(PARAM_SORT) @Sort sort: String?,
                    @Query(PARAM_ORDER) @Order order: String?,
                    @Query(PARAM_PAGE) page: Int?,
                    @Query(PARAM_PER_PAGE) perPage: Int?): Call<UsersResponse>

    /** Выполняет поиск топовых пользователей.
     *
     * @param page запрашиваемая страница, необязательный (равно 1)
     * @param perPage число результатов на странице, необязательный (равно 30)
     *
     * @see <a href="https://developer.github.com/v3/search/#search-users">Search users API</a>
     * @see <a href="https://developer.github.com/v3/#pagination">Pagination</a>
     */
    @GET(PATH_SEARCH_TOP_USERS)
    fun getTopUsers(@Query(PARAM_PAGE) page: Int?,
                    @Query(PARAM_PER_PAGE) perPage: Int?): Call<UsersResponse>

    /** Возвращает всю информацию о конкретном пользователе.
     *
     * @param username имя пользователя
     *
     * @see <a href="https://developer.github.com/v3/users/#get-a-single-user">Get a single user</a>
     *
     */
    @GET(PATH_USER)
    fun getSingleUser(@Path(PARAM_USERNAME) username: String): Call<UserItem?>

}
