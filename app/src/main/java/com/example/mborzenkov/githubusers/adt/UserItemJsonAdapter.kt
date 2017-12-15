package com.example.mborzenkov.githubusers.adt

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

/** Адаптер для конвертации UserItem в JSON и обратно. */
class UserItemJsonAdapter {

    /** Конвертирует Json объект в объект UserItem.
     *
     * @param jsonItem объект json
     *
     * @return объект UserItem
     */
    @FromJson @Suppress("unused")
    fun fromJson(jsonItem: UserItemJson): UserItem = UserItem(
            jsonItem.login ?: "",
            jsonItem.name ?: "",
            jsonItem.location ?: "",
            jsonItem.email ?: "",
            jsonItem.avatar_url ?: "",
            jsonItem.html_url ?: "",
            jsonItem.bio ?: "",
            jsonItem.followers ?: 0,
            jsonItem.blog ?: "")

    @ToJson @Suppress("unused")
    fun toJson(item: UserItem): UserItemJson = UserItemJson(
            item.username,
            0,
            item.imageUrl,
            item.profileUrl,
            item.email,
            item.fullname,
            item.location,
            item.bio,
            item.followers,
            item.blog)

    /** Класс, описывающий объект [UserItem], представленный в JSON строке. */
    class UserItemJson(
            val login: String?,
            val id: Int?,
            val avatar_url: String?,
            val html_url: String?,
            val email: String?,
            val name: String?,
            val location: String?,
            val bio: String?,
            val followers: Int?,
            val blog: String?)

}