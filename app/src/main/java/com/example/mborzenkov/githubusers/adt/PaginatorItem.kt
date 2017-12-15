package com.example.mborzenkov.githubusers.adt

import com.example.mborzenkov.githubusers.adapter.ViewType

/** Тип данных, представляющий пагинатор.
 *
 * @param prefix property [prefix]
 * @param page property [page]
 * @param totalPages property [totalPages]
 *
 * @property prefix Строковый префикс пагинатора, например "Страница".
 * @property page Текущая страница пагинатора.
 * @property totalPages Общее число страниц в пагинаторе.
 */
data class PaginatorItem(
        val prefix: String,
        var page: Int,
        var totalPages: Int
) : ViewType {

    override fun getViewType(): Int = ViewType.Value.PAGINATOR

}