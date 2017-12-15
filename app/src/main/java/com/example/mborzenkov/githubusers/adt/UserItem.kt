package com.example.mborzenkov.githubusers.adt

import android.os.Parcel
import android.os.Parcelable
import com.example.mborzenkov.githubusers.adapter.ViewType

/** Тип данных, представляющий собой элемент списка пользователей.
 *
 * @param username property [username]
 * @param fullname property [fullname]
 * @param location property [location]
 * @param email property [email]
 * @param imageUrl property [imageUrl]
 * @param profileUrl property [profileUrl]
 * @param bio property [bio]
 * @param followers property [followers]
 * @param blog property [blog]
 *
 * @property username Имя пользователя.
 * @property fullname Полное имя пользователя (обычно имя и фамилия).
 * @property location Место нахождения пользователя (обычно город).
 * @property email Email пользователя.
 * @property imageUrl Ссылка на картинку.
 * @property profileUrl Ссылка на профиль.
 * @property bio Описание пользователя.
 * @property followers Число пользователей.
 * @property blog Ссылка на блог.
 */
data class UserItem(
        val username: String,
        val fullname: String,
        val location: String,
        val email: String,
        val imageUrl: String,
        val profileUrl: String,
        val bio: String,
        val followers: Int,
        val blog: String
) : ViewType, Parcelable {

    companion object {
        /** Вспомогательный объект для конвертации UserItem в Parcel и обратно. */
        @JvmField @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<UserItem> {
            override fun createFromParcel(source: Parcel): UserItem? = UserItem(source)
            override fun newArray(size: Int): Array<out UserItem?> = arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeString(fullname)
        parcel.writeString(location)
        parcel.writeString(email)
        parcel.writeString(imageUrl)
        parcel.writeString(profileUrl)
        parcel.writeString(bio)
        parcel.writeInt(followers)
        parcel.writeString(blog)
    }

    override fun describeContents(): Int = 0

    override fun getViewType() = ViewType.Value.USER

}