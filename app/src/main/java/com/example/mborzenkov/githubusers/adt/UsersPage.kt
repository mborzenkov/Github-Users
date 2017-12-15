package com.example.mborzenkov.githubusers.adt

import android.os.Parcel
import android.os.Parcelable

/** Тип данных, представляющий страницу пользователей.
 *
 * @param number property [number]
 * @param total property [total]
 * @param items property [items]
 *
 * @property number Номер страницы.
 * @property total Общее число страниц.
 * @property items Список пользователей на этой странице.
 */
data class UsersPage(
        val number: Int,
        val total: Int,
        val items: List<UserItem>) : Parcelable {

    companion object {
        /** Вспомогательный объект для конвертации UsersPage в Parcel и обратно. */
        @JvmField @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<UsersPage> {
            override fun createFromParcel(source: Parcel): UsersPage? = UsersPage(source)
            override fun newArray(size: Int): Array<out UsersPage?> = arrayOfNulls(size)
        }

        /** Константа, представляющая пустую страницу. */
        @JvmField
        val EMPTY = UsersPage(0, 0, emptyList())
    }

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            mutableListOf<UserItem>().apply {
                parcel.readTypedList(this, UserItem.CREATOR)
            }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(number)
        parcel.writeInt(total)
        parcel.writeTypedList(items)
    }

    override fun describeContents(): Int = 0

}