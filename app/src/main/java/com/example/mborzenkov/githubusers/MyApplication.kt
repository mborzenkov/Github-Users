package com.example.mborzenkov.githubusers

import android.app.Application
import com.example.mborzenkov.githubusers.networking.dagger.DaggerGithubApiComponent
import com.example.mborzenkov.githubusers.networking.dagger.GithubApiComponent
import com.example.mborzenkov.githubusers.networking.dagger.GithubApiModule
import com.vk.sdk.VKSdk

class MyApplication : Application() {

    /** Компонент для иньекций GithubApi. */
    private var mGithubApiComponent: GithubApiComponent? = null

    override fun onCreate() {
        super.onCreate()
        // Инициализация VKSdk
        VKSdk.initialize(applicationContext)
        // Иньекции
        if (mGithubApiComponent == null) {
            mGithubApiComponent = DaggerGithubApiComponent.builder().githubApiModule(GithubApiModule(null)).build()
        }
    }

    /** Устанавливает новый компонент.
     *
     * @param component новый GithubApiComponent
     */
    fun setGithubApiComponent(component: GithubApiComponent) {
        mGithubApiComponent = component
    }

    /** Возвращает установленный компонент GithubApiComponent.
     * Возможен вызов только после setGithubApiComponent или onCreate()
     *
     * @return установленный компонент
     */
    fun getGithubApiComponent(): GithubApiComponent {
        return mGithubApiComponent!!
    }

}
