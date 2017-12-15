package com.example.mborzenkov.githubusers.activity

import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.transition.TransitionInflater
import android.widget.ImageView
import com.example.mborzenkov.githubusers.R
import com.example.mborzenkov.githubusers.adt.LoginItem
import com.example.mborzenkov.githubusers.adt.UserItem
import com.example.mborzenkov.githubusers.commons.changeFragment
import com.example.mborzenkov.githubusers.fragment.LoginFragment
import com.example.mborzenkov.githubusers.fragment.UserInfoFragment
import com.example.mborzenkov.githubusers.fragment.UserListFragment
import com.example.mborzenkov.githubusers.login.LoginManager

/** Главная Activity, представляющая собой список. */
class MainActivity :
        AppCompatActivity(),
        UserListFragment.UserListCallbacks,
        LoginFragment.LoginCallbacks {


    companion object {

        /////////////////////////
        // Константы

        /** ID контейнера для помещения фрагментов.  */
        @IdRes private const val FRAGMENT_CONTAINER = R.id.fragmentcontainer_mainactivity

        /** Ключ для сохранения mLoginShowed в Saved Instance State. */
        private const val LOGIN_SHOWED_KEY = "mLoginShowed"

        /** Имя SharedElement. */
        const val SHARED_ELEMENT_IMAGE_TRANSITION_NAME = "githubusers_sharedelement_image"

    }


    /////////////////////////
    // Поля объекта

    /** Фрагмент для авторизации. */
    private lateinit var mLoginFragment: LoginFragment
    /** Фрагмент со списоком. */
    private lateinit var mUserListFragment: UserListFragment
    /** Фрагмент с полной информацией о пользователе. */
    private lateinit var mUserInfoFragment: UserInfoFragment

    /** Менеджер авторизации. */
    private lateinit var mLoginManager: LoginManager

    /** Признак, показана ли уже была авторизация. */
    private var mLoginShowed = false


    /////////////////////////
    // Колбеки Activity

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализациия фрагментов
        mLoginFragment = LoginFragment.getInstance(supportFragmentManager)
        mUserListFragment = UserListFragment.getInstance(supportFragmentManager)
        mUserInfoFragment = UserInfoFragment.getInstance(supportFragmentManager)

        // Инициализация LoginManager
        mLoginManager = LoginManager(this)

        if (savedInstanceState == null) {
            // Если это запуск с 0, добавляем UserListFrament
            supportFragmentManager
                    .changeFragment(FRAGMENT_CONTAINER, mUserListFragment, UserListFragment.TAG)
        } else {
            mLoginShowed = savedInstanceState.getBoolean(LOGIN_SHOWED_KEY, false)
        }

    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState?.putBoolean(LOGIN_SHOWED_KEY, mLoginShowed)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStackImmediate()
        } else {
            finish()
        }
    }


    /////////////////////////
    // Колбеки UserListFragment

    override fun onSignInPressed() {
        supportFragmentManager
                .changeFragment(FRAGMENT_CONTAINER, mLoginFragment, LoginFragment.TAG)
    }

    override fun onLogOffPressed() {
        mLoginManager.deauthCurrentUser(this)
    }

    override fun onDataLoaded() {
        if (!mLoginShowed && !mLoginManager.isAuthorized()) {
            supportFragmentManager
                    .changeFragment(FRAGMENT_CONTAINER, mLoginFragment, LoginFragment.TAG)
            mLoginShowed = true
        }
    }

    override fun onUserClick(user: UserItem, sharedElement: ImageView?) {
        mUserInfoFragment.updateUserData(user)

        var shared = sharedElement
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mUserInfoFragment.sharedElementEnterTransition = TransitionInflater.from(this).inflateTransition(android.R.transition.move)
            mUserInfoFragment.enterTransition = TransitionInflater.from(this).inflateTransition(android.R.transition.fade)
        } else {
            shared = null
        }

        supportFragmentManager
                .changeFragment(
                        FRAGMENT_CONTAINER,
                        mUserInfoFragment,
                        UserInfoFragment.TAG,
                        false,
                        shared,
                        SHARED_ELEMENT_IMAGE_TRANSITION_NAME)
    }

    /////////////////////////
    // Колбеки LoginFragment

    override fun onSuccess(user: LoginItem) {
        mLoginManager.authUser(user)
        onBackPressed()
    }

    override fun onCancel() {
        onBackPressed()
    }

}
