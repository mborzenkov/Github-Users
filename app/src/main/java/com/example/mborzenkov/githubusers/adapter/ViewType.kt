package com.example.mborzenkov.githubusers.adapter

/** Интерфейс для передачи данных в Delegate Adapter. */
interface ViewType {

    /** Возвращает тип данных. */
    fun getViewType(): Int

    /** Возможные типы данных. */
    object Value {
        val USER = 1
        val PAGINATOR = 2
        val EMPTY = 3
    }
}