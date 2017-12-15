package com.example.mborzenkov.githubusers.login

import android.content.Context
import android.support.annotation.StringDef
import com.example.mborzenkov.githubusers.adt.LoginItem
import com.example.mborzenkov.githubusers.commons.readFileToString
import com.example.mborzenkov.githubusers.commons.writeStringToFile
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.vk.sdk.VKSdk
import io.reactivex.subjects.BehaviorSubject
import java.io.File

/** Менеджер локальной авторизации. */
class LoginManager(context: Context) {


    /////////////////////////
    // Static

    companion object {

        /** Обзервер для событий авторизации. */
        val stateObserver: BehaviorSubject<LoginItem> = BehaviorSubject.create<LoginItem>()

        /** Файл с информацией о логинах в Shared Preferences. */
        private const val LOGIN_KEY = "com.example.mborzenkov.githubusers.login"
        /** Ключ для сохранения текущего логина в Shared Preferences. */
        private const val LOGIN_LAST_KEY = "last"

        /** Текущий логин.
         * Если равен LoginItem.EMPTY, то значит пользователь не авторизован.
         * Не инициализирован до первой инициализации LoginManager (равен EMPTY).
         */
        private var sCurrentLogin: LoginItem = LoginItem.EMPTY

        /** Варианты аутентификаций. */
        @StringDef(VK, FB, GOOG)
        annotation class AuthType

        // Варианты аутентификаций
        const val VK = "vk"
        const val FB = "fb"
        const val GOOG = "goog"

    }


    /////////////////////////
    // Поля объекта

    /** Shared Preferences с информацией о логинах. */
    private val mSharedPrefs = context.getSharedPreferences(LOGIN_KEY, Context.MODE_PRIVATE)
    /** Файл со всеми сохраненными логинами. */
    private val mFileLoginAll = File(context.filesDir, "logins")

    init {
        getCurrentLogin()
        stateObserver.onNext(sCurrentLogin)
    }


    /////////////////////////
    // Mutators

    /** Устанавливает нового текущего пользователя, добавляет в SharedPref и в файл с логинами
     *
     * @param login новый пользователь
     */
    fun authUser(login: LoginItem) {
        mSharedPrefs.edit().putString(LOGIN_LAST_KEY, login.uid).apply()
        sCurrentLogin = saveLogin(login)
        stateObserver.onNext(sCurrentLogin)
    }

    /** Деавторизует текущего пользователя, очищает в SharedPref, файл с логинами не изменяется.
     * Также деавторизует из всех социальных сетей.
     *
     * @param context
     */
    fun deauthCurrentUser(context: Context) {
        sCurrentLogin = LoginItem.EMPTY
        mSharedPrefs.edit().putString(LOGIN_LAST_KEY, "").apply()
        VKSdk.logout()
        LoginManager.getInstance().logOut()
        GoogleSignIn.getClient(context, GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build()).signOut()
        stateObserver.onNext(sCurrentLogin)
    }

    /** Полностью очищает историю логинов и деавторизует текущего пользователя.
     *
     * @param context контекст
     */
    fun clearLoginData(context: Context) {
        deauthCurrentUser(context)
        if (mFileLoginAll.exists()) {
            mFileLoginAll.delete()
        }
    }

    /** Сохраняет логин в список пользователей.
     * В случае, если пользователь уже был добавлен ранее, игнорирует изменения.
     *
     * @param login логин для сохранения
     *
     * @return login, если пользователя не было или LoginItem, полученный из списка пользователей
     */
    private fun saveLogin(login: LoginItem): LoginItem {
        val savedLogins = getAllSavedLogins()
        return if (savedLogins.add(login)) {
            val adapter = Moshi.Builder().build().adapter<Set<LoginItem>>(Types.newParameterizedType(Set::class.java, LoginItem::class.java))
            mFileLoginAll.writeStringToFile(adapter.toJson(savedLogins))
            login
        } else {
            savedLogins.find { it == login }!!
        }
    }


    /////////////////////////
    // Observers

    /** Определяет, авторизован ли пользователь.
     *
     * @return true, если авторизован, иначе false
     */
    fun isAuthorized(): Boolean {
        return getCurrentLogin() != LoginItem.EMPTY
    }

    /** Возвращает логин текущего авторизованного пользователя.
     * Также устанавливает его в sCurrentLogin.
     *
     * @return логин текущего авторизованного пользователя или LoginItem.EMPTY, если пользователь
     *              не авторизован
     */
    private fun getCurrentLogin(): LoginItem {
        if (sCurrentLogin == LoginItem.EMPTY) {
            val curUid = mSharedPrefs.getString(LOGIN_LAST_KEY, "")
            sCurrentLogin = if (curUid.isNotEmpty()) getAllSavedLogins().find( { it.uid == curUid } ) ?: LoginItem.EMPTY else LoginItem.EMPTY
        }
        return sCurrentLogin
    }

    /** Возвращает все сохраненные логины.
     *
     * @return все сохраненные логины в базе пользователей
     */
    private fun getAllSavedLogins(): MutableSet<LoginItem> {
        val adapter = Moshi.Builder().build().adapter<MutableSet<LoginItem>>(Types.newParameterizedType(MutableSet::class.java, LoginItem::class.java))
        val fileString = mFileLoginAll.readFileToString()
        return if (fileString.isNotBlank()) adapter.fromJson(fileString) else mutableSetOf()
    }

}
