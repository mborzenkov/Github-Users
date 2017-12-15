package com.example.mborzenkov.githubusers.login

import android.content.Context
import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.webkit.URLUtil
import com.example.mborzenkov.githubusers.adt.LoginItem
import com.example.mborzenkov.githubusers.commons.loadImageFromWeb
import com.facebook.AccessToken
import com.facebook.Profile
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKSdk
import com.vk.sdk.VKServiceActivity
import com.vk.sdk.api.VKResponse
import com.vk.sdk.api.model.VKApiUserFull
import com.vk.sdk.api.model.VKList
import java.io.File

/** Вспомогательный класс для авторизации через соцсети. */
class SocialAuthHelper {

    /////////////////////////
    // Инициализаторы

    /** Создает интент для запуска авторизации через VK.
     * Использовать через startActivityForResult
     *
     * @param activity источник интента
     *
     * @return внешний интент для авторизации через вк
     */
    fun getVkIntent(activity: FragmentActivity): Intent {
        val intent = Intent(activity, VKServiceActivity::class.java)
        intent.putExtra("arg1", "Authorization")
        val scopes: ArrayList<String> = arrayListOf()
        intent.putStringArrayListExtra("arg2", scopes)
        intent.putExtra("arg4", VKSdk.isCustomInitialize())
        return intent
    }

    /** Создает интент для запуска авторизации через Google.
     * Использовать через startActivityForResult
     *
     * @param activity источник интента
     *
     * @return внешний интент для авторизации через google
     */
    fun getGoogleIntent(activity: FragmentActivity): Intent {
        return GoogleSignIn.getClient(
                activity,
                GoogleSignInOptions
                        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .build())
                .signInIntent
    }


    /////////////////////////
    // Парсеры

    /** Разбирает результат авторизации Facebook.
     *
     * @param context контекст
     * @param result результат авторизации определенного формата
     * @param onComplete интерфейс для оповещения об успешном завершении авторизации
     */
    fun parseFacebookLoginResult(context: Context, result: LoginResult, onComplete: (LoginItem) -> Unit) {
        val profile = Profile.getCurrentProfile()
        profile?.let {
            val login = LoginItem.newLogin(LoginManager.FB + profile.id)
            login.fullname = profile.name
            login.authType = LoginManager.FB
            login.token = AccessToken.getCurrentAccessToken().token

            val imgUrl = profile.getProfilePictureUri(450, 450).toString()
            if (imgUrl.isNotBlank()) {
                val imageFileName = URLUtil.guessFileName(imgUrl, null, null)
                File(context.filesDir, imageFileName).loadImageFromWeb(
                        context,
                        imgUrl, {
                    localFile ->
                    login.imageUrl = localFile
                    onComplete(login)
                })
            } else {
                onComplete(login)
            }
        }
    }

    /** Разбирает результат авторизации VK.
     *
     * @param context контекст
     * @param result результат авторизации определенного формата
     * @param onComplete интерфейс для оповещения об успешном завершении авторизации
     */
    fun parseVkLoginResult(context: Context, result: VKResponse, token: VKAccessToken, onComplete: (LoginItem) -> Unit) {
        val listUserData = result.parsedModel as VKList<VKApiUserFull>
        val userData = listUserData[0]
        val login = LoginItem.newLogin(LoginManager.VK + token.userId)
        login.fullname = (userData.first_name + " " + userData.last_name).trim()
        login.authType = LoginManager.VK
        login.token = token.accessToken

        val imgUrl = userData.photo_max_orig
        if (imgUrl.isNotBlank()) {
            val imageFileName = URLUtil.guessFileName(imgUrl, null, null)
            File(context.filesDir, imageFileName).loadImageFromWeb(
                    context,
                    imgUrl, {
                localFile ->
                login.imageUrl = localFile
                onComplete(login)
            })
        } else {
            onComplete(login)
        }
    }

    /** Разбирает результат авторизации Google.
     *
     * @param context контекст
     * @param account результат авторизации определенного формата (аккаунт Google)
     * @param onComplete интерфейс для оповещения об успешном завершении авторизации
     */
    fun parseGoogleLoginResult(context: Context, account: GoogleSignInAccount, onComplete: (LoginItem) -> Unit) {
        val login = LoginItem.newLogin(LoginManager.GOOG + account.id)
        login.fullname = account.displayName.toString()
        login.authType = LoginManager.GOOG
        login.token = account.idToken.toString()

        val imgUrl = account.photoUrl?.toString() ?: ""
        if (imgUrl.isNotBlank()) {
            val imageFileName = URLUtil.guessFileName(imgUrl, null, null)
            File(context.filesDir, imageFileName).loadImageFromWeb(
                    context,
                    imgUrl, {
                localFile ->
                login.imageUrl = localFile
                onComplete(login)
            })
        } else {
            onComplete(login)
        }
    }

}