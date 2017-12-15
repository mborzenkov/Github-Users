package com.example.mborzenkov.githubusers.networking

import android.content.UriMatcher
import android.net.Uri
import com.example.mborzenkov.githubusers.commons.readFileToString
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.io.File

/** Класс [Dispatcher] для обработки запросов к fake-серверу.
 */
class GithubApiMockDispatcher(serverAuthority: String) : Dispatcher() {

    companion object {

        // Константы
        private const val CODE_SEARCH = 100
        private const val CODE_USER = 101
        private const val RESPONSE_OK = 200
        private const val RESPONSE_NOT_FOUND = 404
        private const val RESPONSE_BAD_REQUEST = 400
        private const val METHOD_GET = "GET"

        private val USERS_RESPONSE = File(GithubApiMockDispatcher::class.java.classLoader.getResource("MockServerUsersResponse.txt").toURI()).readFileToString()
        private val SINGLE_USER_RESPONSE = File(GithubApiMockDispatcher::class.java.classLoader.getResource("MockServerSingleUserResponse.txt").toURI()).readFileToString()

        /** Dispatcher который всегда возвращает пустой MockResponse.  */
        class EmptyDispatcher : Dispatcher() {
            @Throws(InterruptedException::class)
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse()
            }
        }

        /** Dispatcher который всегда возвращает OK и ответ неправильного формата.  */
        internal class MalformedDispatcher : Dispatcher() {
            @Throws(InterruptedException::class)
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse().setResponseCode(RESPONSE_OK)
                        .setBody("{\"somebody\":\"oncetoldme\",\"theworld\":\"isgonnarollme\"}")
            }
        }

        /** Dispatcher который всегда возвращает OK и ошибку.  */
        internal class ErrorDispatcher : Dispatcher() {
            @Throws(InterruptedException::class)
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse().setResponseCode(RESPONSE_OK)
                        .setBody("{\"message\":\"Not Found\"}")
            }
        }

        /** Dispatcher который всегда возвращает OK и пустой items.  */
        internal class EmptyDataDispatcher : Dispatcher() {
            @Throws(InterruptedException::class)
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse().setResponseCode(RESPONSE_OK)
                        .setBody("{\"total_count\": 0,\"incomplete_results\":false,\"items\": []}")
            }
        }

        /** Dispatcher который всегда возвращает BAD REQUEST и пустую Body.  */
        internal class EmptyBodyDispatcher : Dispatcher() {
            @Throws(InterruptedException::class)
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse().setResponseCode(RESPONSE_BAD_REQUEST).setBody("")
            }
        }
    }

    /** Матчер для Uri.  */
    private val mUriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    init {
        mUriMatcher.addURI(serverAuthority, "search/users", CODE_SEARCH)
        mUriMatcher.addURI(serverAuthority, "users/*", CODE_USER)
    }

    override fun dispatch(request: RecordedRequest?): MockResponse {

        // Запоминаем метод, запрошенную Uri, userId и itemId
        val method = request?.method
        val requestUri = Uri.parse(request?.getRequestUrl().toString())

        // Дефолтный ответ 404
        var response = MockResponse().setResponseCode(RESPONSE_NOT_FOUND)

        when (mUriMatcher.match(requestUri)) {
            CODE_SEARCH -> {
                if (method == METHOD_GET) {
                    response = MockResponse()
                            .setResponseCode(RESPONSE_OK)
                            .setBody(USERS_RESPONSE)
                }
            }
            CODE_USER -> {
                if (method == METHOD_GET) {
                    response = MockResponse()
                            .setResponseCode(RESPONSE_OK)
                            .setBody(SINGLE_USER_RESPONSE)
                }
            }
        }

        return response

    }
}