package com.example.mborzenkov.githubusers.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

/** Интерфейс шаблона DelegateAdapter. */
interface ViewTypeDelegateAdapter {

    /** Выполняет создание ViewHolder.
     *
     * @param parent ViewGroup в который нужно добавить новый View, после того, как будет выполнена
     *                      привязка onBindViewHolder
     *
     * @return
     */
    fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder

    /** Выполняет обновление ViewHolder на основании объекта.
     *
     * @param holder ViewHolder, который необходимо обновить
     * @param item объект, содержащий данные
     */
    fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType)

}