package com.example.mborzenkov.githubusers.networking.dagger

import com.example.mborzenkov.githubusers.networking.GithubApiImpl
import dagger.Component
import javax.inject.Singleton

/** Интерфейс компонента для иньекций. */
@Singleton
@Component(modules = [(GithubApiModule::class)])
interface GithubApiComponent {
    /** Выполняет иньекцию в api. */
    fun inject(api: GithubApiImpl)
}