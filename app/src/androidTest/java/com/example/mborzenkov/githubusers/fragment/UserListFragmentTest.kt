package com.example.mborzenkov.githubusers.fragment

import android.content.Intent
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.pressBack
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.mborzenkov.githubusers.R
import com.example.mborzenkov.githubusers.activity.MainActivity
import com.example.mborzenkov.githubusers.login.LoginManager
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.anyOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Тестирует UserListFragment. */
@RunWith(AndroidJUnit4::class)
class UserListFragmentTest {

    private val ONSTART_SLEEP = 4000
    private val ANIM_SLEEP = 1500

    @get:Rule
    val mActivityTestRule = ActivityTestRule<MainActivity>(MainActivity::class.java, false, false)

    @Test
    fun testItemListFragmentIsDisplayed() {

        mActivityTestRule.launchActivity(Intent())

        clearState()

        run {
            checkAuthDisplayed()
        }

        pressBack()

        try {
            Thread.sleep(ANIM_SLEEP.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        run {
            checkListDisplayed()
        }

    }

    @Test
    fun testAuthButtonClick() {

        mActivityTestRule.launchActivity(Intent())

        clearState()

        pressBack()

        try {
            Thread.sleep(ANIM_SLEEP.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        checkListDisplayed()

        run {
            val buttonLogin = onView(allOf<View>(withId(R.id.action_userlist_signin), isDisplayed()))
            buttonLogin.perform(click())
        }
        try {
            Thread.sleep(ANIM_SLEEP.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        run {
            checkAuthDisplayed()
        }

    }

    @Test
    fun testMoreInfoClick() {

        mActivityTestRule.launchActivity(Intent())

        clearState()

        pressBack()

        try {
            Thread.sleep(ANIM_SLEEP.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        checkListDisplayed()

        run {
            val userList = onView(allOf(withId(R.id.recyclerview_userlist), isDisplayed()))
            userList.perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        }
        try {
            Thread.sleep(ANIM_SLEEP.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        run {
            checkInfoDisplayed()
        }

    }

    private fun checkListDisplayed() {
        // Показывается список
        val userList = onView(allOf<View>(withId(R.id.swiperefreshlayout_userlist), isDisplayed()))
        userList.check(matches(isDisplayed()))

        val toolBar = onView(allOf<View>(withId(R.id.toolbar_userlist), isDisplayed()))
        toolBar.check(matches(isDisplayed()))

        val searchField = onView(allOf<View>(withId(R.id.et_userlist_search), isDisplayed()))
        searchField.check(matches(isDisplayed()))
    }

    private fun checkAuthDisplayed() {
        // Показывается окно авторизации
        val buttonFb = onView(allOf<View>(withId(R.id.button_login_facebook), isDisplayed()))
        buttonFb.check(matches(isDisplayed()))

        val buttonVk = onView(allOf<View>(withId(R.id.button_login_vk), isDisplayed()))
        buttonVk.check(matches(isDisplayed()))

        val buttonGoog = onView(allOf<View>(withId(R.id.button_login_google), isDisplayed()))
        buttonGoog.check(matches(isDisplayed()))

        val buttonCancel = onView(allOf<View>(withId(R.id.button_login_cancel), isDisplayed()))
        buttonCancel.check(matches(isDisplayed()))
    }

    private fun checkInfoDisplayed() {
        // Показывается информация о пользователе
        onView(allOf<View>(withId(R.id.tv_userinfo_username), isDisplayed())).check(matches(isDisplayed()))
        onView(allOf<View>(withId(R.id.iv_userinfo_image), isDisplayed())).check(matches(isDisplayed()))
    }

    private fun clearState() {

        val activity = mActivityTestRule.activity

        val manager = LoginManager(activity)
        manager.deauthCurrentUser(activity)
        manager.clearLoginData(activity)

        try {
            Thread.sleep(ONSTART_SLEEP.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }

}