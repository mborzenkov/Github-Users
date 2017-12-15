package com.example.mborzenkov.githubusers.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mborzenkov.githubusers.R
import com.example.mborzenkov.githubusers.adt.LoginItem
import com.example.mborzenkov.githubusers.commons.inflate
import com.example.mborzenkov.githubusers.login.SocialAuthHelper
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKSdk
import com.vk.sdk.VKServiceActivity
import com.vk.sdk.api.*
import kotlinx.android.synthetic.main.fragment_login.*


/** Фрагмент с функционалом авторизации через соцсети. */
class LoginFragment : Fragment() {

    /////////////////////////
    // Static

    /** Интерфейс для оповещений о событиях в фрагменте. */
    interface LoginCallbacks{
        /** Вызывается при успешной авторизации.
         *
         * @param user данные авторизованного пользователя
         */
        fun onSuccess(user: LoginItem)
        /** Вызывается при отмене авторизации. */
        fun onCancel()
    }

    companion object {

        /** Layout фрагмента. */
        private const val FRAGMENT_LAYOUT = R.layout.fragment_login

        /** Код запроса для авторизации Google. */
        const val REQUESTCODE_GOOGLE_SIGNIN = 11

        /** Ключ к получению фотографии профиля ВКонтакте. */
        const val VK_PHOTO_KEY = "photo_max_orig"

        /** TAG фрагмента для фрагмент менеджера. */
        const val TAG = "fragment_login"

        /** Возвращает уже созданный ранее объект LoginFragment или создает новый, если такого нет.
         * Для создания объектов следует всегда использовать этот метод.
         * Не помещает объект в FragmentManager.
         * При помещении объекта в FragmentManager, следует использовать тэг TAG.
         *
         * @param manager менеджер для поиска фрагментов по тэгу
         *
         * @return новый объект LoginFragment
         */
        fun getInstance(manager: FragmentManager): LoginFragment {
            return manager.findFragmentByTag(TAG) as LoginFragment? ?: LoginFragment()
        }

    }


    /////////////////////////
    // Поля объекта

    /** Объект для колбеков о событиях во фрагменте.  */
    private var mCallbacks: LoginCallbacks? = null

    /** Менеджер колбеков для фейсбука. */
    private lateinit var mFacebookCallbackManager: CallbackManager

    /** Помощник авторизации. */
    private val mAuthHelper = SocialAuthHelper()


    /////////////////////////
    // Колбеки Fragment

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        context?.let {
            mCallbacks = context as LoginCallbacks
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(false)
        return container?.inflate(FRAGMENT_LAYOUT)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupSocial()
        // Кнопка пропустить
        button_login_cancel.setOnClickListener({ mCallbacks?.onCancel() })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            requestCode == REQUESTCODE_GOOGLE_SIGNIN -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    context?.let {
                        mAuthHelper.parseGoogleLoginResult(
                                it,
                                account,
                                { login -> mCallbacks?.onSuccess(login) }
                        )
                    }
                } catch (e: ApiException) {
                    Log.w(TAG, "signInResult:failed code=" + e.statusCode)
                }
            }

            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data) -> return

            VKSdk.onActivityResult(
                    requestCode,
                    resultCode,
                    data,
                    object : VKCallback<VKAccessToken> {
                        // Пользователь успешно авторизовался
                        override fun onResult(authResult: VKAccessToken) {
                            val request = VKApi.users().get(
                                    VKParameters.from(
                                            VKApiConst.USER_IDS,
                                            authResult.userId,
                                            VKApiConst.FIELDS,
                                            VK_PHOTO_KEY))
                            request.executeWithListener( object : VKRequest.VKRequestListener() {
                                override fun onComplete(response: VKResponse) {
                                    context?.let {
                                        mAuthHelper.parseVkLoginResult(
                                                it,
                                                response,
                                                authResult,
                                                { login -> mCallbacks?.onSuccess(login) }
                                        )
                                    }
                                }
                            })
                        }

                        // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
                        override fun onError(error: VKError) {}
                    }) -> return // уже загружена информация
        }
    }


    /////////////////////////
    // Инициализация

    /** Инициализирует авторизацию через соцсети. */
    private fun setupSocial() {
        // Инициализация авторизации Facebook
        mFacebookCallbackManager = CallbackManager.Factory.create()
        button_login_facebook.setReadPermissions("public_profile")
        button_login_facebook.fragment = this
        button_login_facebook.registerCallback(
                mFacebookCallbackManager,
                object: FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult) {
                        context?.let {
                            mAuthHelper.parseFacebookLoginResult(
                                    it,
                                    result,
                                    { login -> mCallbacks?.onSuccess(login) }
                            )
                        }
                    }
                    override fun onCancel() { }
                    override fun onError(error: FacebookException?) { }
                }
        )

        // Инициализация авторизации ВКонтакте
        button_login_vk.setOnClickListener({ startActivityForResult(mAuthHelper.getVkIntent(activity!!), VKServiceActivity.VKServiceType.Authorization.outerCode) })

        // Инициализация авторизации Google
        button_login_google.setOnClickListener({ startActivityForResult(mAuthHelper.getGoogleIntent(activity!!), REQUESTCODE_GOOGLE_SIGNIN) })
    }

}