package com.example.mborzenkov.githubusers.networking

import com.example.mborzenkov.githubusers.BuildConfig
import com.example.mborzenkov.githubusers.adt.UserItem
import com.example.mborzenkov.githubusers.networking.dagger.DaggerGithubApiComponent
import com.example.mborzenkov.githubusers.networking.dagger.GithubApiModule
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.EOFException

import java.io.IOException

/** Тестирует [UsersManager]. */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class GithubApiTest {

    companion object {

        // Пользователи
        @JvmStatic
        private val USER1 = UserItem("username1", "", "", "", "", "", "", 0, "")
        @JvmStatic
        private val USER2 = USER1.copy("username2")
    }

    /** Fake-сервер. */
    val mMockServer = MockWebServer()
    /** Api. */
    lateinit var mGithubApi: GithubApiImpl

    /** Создает, запускает и подключает fake-сервер. */
    @Before
    fun onStart() {
        mMockServer.start()
        val serverUrl = mMockServer.url("")
        mMockServer.setDispatcher(GithubApiMockDispatcher(serverUrl.host() + ":" + serverUrl.port()))
        val component = DaggerGithubApiComponent.builder().githubApiModule(GithubApiModule(serverUrl)).build()
        mGithubApi = GithubApiImpl(component)
        component.inject(mGithubApi)
        // ShadowLog.stream = System.out; // Раскомментируйте строчку для вывода в лог всех обращений
    }

    /** Выключает fake-сервер.  */
    @After
    @Throws(IOException::class)
    fun onStop() {
        mMockServer.shutdown()
    }

    @Test
    fun testNormalResponse() {
        // Проверяем топ юзеров
        val responseTop = mGithubApi.getTopUsers(1).execute()
        assertTrue(responseTop.isSuccessful)
        assertTrue(responseTop.body() != null)
        val bodyTop = responseTop.body()!!
        // Должны получить 3, в items тоже 3
        assertEquals(3, bodyTop.total_count)
        assertTrue(bodyTop.items != null)
        assertEquals(3, bodyTop.items!!.size)

        // Проверяем одного пользователя
        val responseSingle = mGithubApi.getSingleUser("mborzenkov").execute()
        assertTrue(responseSingle.isSuccessful)
        assertTrue(responseSingle.body() != null)
        val bodySingle = responseSingle.body()!!
        // Должны получить указанный username
        assertEquals("mborzenkov", bodySingle.username)

        // Проверяем поиск
        val responseSearch = mGithubApi.searchUsers("query").execute()
        assertTrue(responseSearch.isSuccessful)
        assertTrue(responseSearch.body() != null)
        val bodySearch = responseTop.body()!!
        // Должны получить 3, в items тоже 3
        assertEquals(3, bodySearch.total_count)
        assertTrue(bodySearch.items != null)
        assertEquals(3, bodySearch.items!!.size)
    }

    @Test
    fun testEmptyResponse() {
        mMockServer.setDispatcher(GithubApiMockDispatcher.Companion.EmptyDispatcher())

        // Проверяем топ юзеров
        var exceptionCaught: Boolean = false
        try {
            val responseTop = mGithubApi.getTopUsers(1).execute()
            exceptionCaught = false
        } catch (e: IOException) {
            exceptionCaught = true
        }
        assertTrue(exceptionCaught)

        // Проверяем одного пользователя
        try {
            val responseSingle = mGithubApi.getSingleUser("mborzenkov").execute()
            exceptionCaught = false
        } catch (e: IOException) {
            exceptionCaught = true
        }
        assertTrue(exceptionCaught)

        // Проверяем поиск
        try {
            val responseSearch = mGithubApi.searchUsers("12").execute()
            exceptionCaught = false
        } catch (e: IOException) {
            exceptionCaught = true
        }
        assertTrue(exceptionCaught)

    }

    @Test
    fun testMalformedResponse() {
        mMockServer.setDispatcher(GithubApiMockDispatcher.Companion.MalformedDispatcher())

        // Проверяем топ юзеров
        val responseTop = mGithubApi.getTopUsers(1).execute()
        assertTrue(responseTop.isSuccessful)
        assertTrue(responseTop.body() != null)
        val bodyTop = responseTop.body()!!
        assertEquals(0, bodyTop.total_count)
        assertEquals(null, bodyTop.items)

        // Проверяем одного пользователя
        val responseSingle = mGithubApi.getSingleUser("mborzenkov").execute()
        assertTrue(responseSingle.isSuccessful)
        assertTrue(responseSingle.body() != null)
        val bodySingle = responseSingle.body()!!
        assertEquals("", bodySingle.username)

        // Проверяем поиск
        val responseSearch = mGithubApi.searchUsers("query").execute()
        assertTrue(responseSearch.isSuccessful)
        assertTrue(responseSearch.body() != null)
        val bodySearch = responseTop.body()!!
        assertEquals(0, bodySearch.total_count)
        assertEquals(null, bodySearch.items)
    }

    @Test
    fun testErrorResponse() {
        mMockServer.setDispatcher(GithubApiMockDispatcher.Companion.ErrorDispatcher())

        // Проверяем топ юзеров
        val responseTop = mGithubApi.getTopUsers(1).execute()
        assertTrue(responseTop.isSuccessful)
        assertTrue(responseTop.body() != null)
        val bodyTop = responseTop.body()!!
        assertEquals(0, bodyTop.total_count)
        assertEquals(null, bodyTop.items)

        // Проверяем одного пользователя
        val responseSingle = mGithubApi.getSingleUser("mborzenkov").execute()
        assertTrue(responseSingle.isSuccessful)
        assertTrue(responseSingle.body() != null)
        val bodySingle = responseSingle.body()!!
        assertEquals("", bodySingle.username)

        // Проверяем поиск
        val responseSearch = mGithubApi.searchUsers("query").execute()
        assertTrue(responseSearch.isSuccessful)
        assertTrue(responseSearch.body() != null)
        val bodySearch = responseTop.body()!!
        assertEquals(0, bodySearch.total_count)
        assertEquals(null, bodySearch.items)
    }

    @Test
    fun testEmptyDataResponse() {
        mMockServer.setDispatcher(GithubApiMockDispatcher.Companion.EmptyDataDispatcher())


        // Проверяем топ юзеров
        val responseTop = mGithubApi.getTopUsers(1).execute()
        assertTrue(responseTop.isSuccessful)
        assertTrue(responseTop.body() != null)
        val bodyTop = responseTop.body()!!
        assertEquals(0, bodyTop.total_count)
        assertTrue(bodyTop.items!!.isEmpty())

        // Проверяем одного пользователя
        val responseSingle = mGithubApi.getSingleUser("mborzenkov").execute()
        assertTrue(responseSingle.isSuccessful)
        assertTrue(responseSingle.body() != null)
        val bodySingle = responseSingle.body()!!
        assertEquals("", bodySingle.username)

        // Проверяем поиск
        val responseSearch = mGithubApi.searchUsers("query").execute()
        assertTrue(responseSearch.isSuccessful)
        assertTrue(responseSearch.body() != null)
        val bodySearch = responseTop.body()!!
        assertEquals(0, bodySearch.total_count)
        assertTrue(bodyTop.items!!.isEmpty())
    }

    @Test
    fun testEmptyBodyResponse() {
        mMockServer.setDispatcher(GithubApiMockDispatcher.Companion.EmptyBodyDispatcher())

        // Проверяем топ юзеров
        val responseTop = mGithubApi.getTopUsers(1).execute()
        assertTrue(!responseTop.isSuccessful)
        assertEquals(null, responseTop.body())

        // Проверяем одного пользователя
        val responseSingle = mGithubApi.getSingleUser("mborzenkov").execute()
        assertTrue(!responseSingle.isSuccessful)
        assertEquals(null, responseSingle.body())

        // Проверяем поиск
        val responseSearch = mGithubApi.searchUsers("query").execute()
        assertTrue(!responseSearch.isSuccessful)
        assertEquals(null, responseSearch.body())

    }




}