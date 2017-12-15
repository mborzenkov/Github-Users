package com.example.mborzenkov.githubusers.adt

import com.squareup.moshi.Moshi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Тестирует UserItemJsonAdapter. */
class UserItemJsonAdapterTest {

    companion object {

        @JvmStatic
        private val USER = UserItem(
                "username",
                "ful",
                "looc",
                "ema",
                "https://pp.userapi.com/c637816/v637816774/1905a/oP-rvarGbzU.jpg",
                "https://github.com/mborzenkov",
                "bio my bio \n bio",
                100,
                "https://vk.com/mborzenkov")

        @JvmStatic
        private val USER_EMPTY_FIELDS = UserItem(
                "username",
                "",
                "",
                "",
                "",
                "",
                "",
                0,
                "")
    }

    @Test
    fun testManual() {
        val jsonAdapter = UserItemJsonAdapter()
        assertTrue(USER.trulyEquals(jsonAdapter.fromJson(jsonAdapter.toJson(USER))))
    }

    @Test
    fun testWithMoshi() {
        val moshi = Moshi.Builder().add(UserItemJsonAdapter()).build()
        val jsonAdapter = moshi.adapter<UserItem>(UserItem::class.java)

        val json = jsonAdapter.toJson(USER)
        val itemFromJson = jsonAdapter.fromJson(json)
        assertTrue(USER.trulyEquals(itemFromJson))
    }

    @Test
    fun testSkipFields() {
        val moshi = Moshi.Builder().add(UserItemJsonAdapter()).build()
        val jsonAdapter = moshi.adapter<UserItem>(UserItem::class.java)

        val json = jsonAdapter.toJson(USER_EMPTY_FIELDS)
        val itemFromJson = jsonAdapter.fromJson(json)
        assertTrue(USER_EMPTY_FIELDS.trulyEquals(itemFromJson))

        val stringSkipFields = "{\"login\":\"username\"}"
        assertTrue(USER_EMPTY_FIELDS.trulyEquals(jsonAdapter.fromJson(stringSkipFields)))
    }

}