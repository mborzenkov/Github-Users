package com.example.mborzenkov.githubusers.adt

import android.os.Parcel
import android.os.Parcelable
import com.example.mborzenkov.githubusers.BuildConfig
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

/** Тестирует UserItem реализацию Parcelable. */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class UserItemParcelableTest {

    companion object {

        private lateinit var sParcel: Parcel

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
    }

    /** Создает Parcel.  */
    @Before
    fun initParcel() {
        sParcel = Parcel.obtain()
    }

    /** Уничтожает Parcel.  */
    @After
    fun recycleParcel() {
        sParcel.recycle()
    }

    @Test
    fun testParcelable() {
        USER.writeToParcel(sParcel, USER.describeContents())
        sParcel.setDataPosition(0)

        val itemFromParcel = UserItem.CREATOR.createFromParcel(sParcel)
        assertTrue(USER.trulyEquals(itemFromParcel))
    }

    @Test
    fun testParcelableArray() {
        val user2 = USER.copy("username2")

        val itemParcelableArray = UserItem.CREATOR.newArray(2)
        itemParcelableArray[0] = USER
        itemParcelableArray[1] = user2
        sParcel.writeParcelableArray(itemParcelableArray, 0)
        sParcel.setDataPosition(0)

        val parcelableArray = sParcel.readParcelableArray(UserItem::class.java.classLoader)
        val parcelableArrayFromParcel = Arrays.copyOf<UserItem, Parcelable>(
                parcelableArray, parcelableArray.size, Array<UserItem>::class.java)
        assertTrue(USER.trulyEquals(parcelableArrayFromParcel[0]))
        assertTrue(user2.trulyEquals(parcelableArrayFromParcel[1]))
    }

}