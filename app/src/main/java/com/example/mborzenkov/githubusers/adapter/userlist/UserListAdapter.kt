package com.example.mborzenkov.githubusers.adapter.userlist

import android.widget.ImageView
import com.example.mborzenkov.githubusers.adapter.BasicAdapter
import com.example.mborzenkov.githubusers.adapter.ViewType
import com.example.mborzenkov.githubusers.adt.PaginatorItem
import com.example.mborzenkov.githubusers.adt.UserItem
import com.example.mborzenkov.githubusers.adt.UsersPage

/** Адаптер для UserList, верхний уровень шаблона DelegateAdapter.
 *
 * @param eventHandler обработчик событий в адаптере
 * @param paginatorPrefix префикс для пагинатора (например, "Страница")
 * @param totalPages суммарное количество страниц в пагинаторе
 */
class UserListAdapter(eventHandler: UserListAdapterEventHandler,
                      paginatorPrefix: String,
                      totalPages: Int = 0): BasicAdapter() {


    /** Интерфейс обработчика событий в пагинаторе. */
    interface UserListAdapterEventHandler {
        /** Вызывается при нажатии на кнопку "назад". */
        fun onPrevClick()
        /** Вызывается при нажатии на кнопку "вперед". */
        fun onNextClick()
        /** Вызывается при нажатии на элемент списка.
         *
         * @param user элемент, на который нажали
         * @param sharedElement Shared Element для перемещения в другой фрагмент
         */
        fun onUserClick(user: UserItem, sharedElement: ImageView?)
    }


    /** Данные для пагинатора. */
    private val paginator = PaginatorItem(paginatorPrefix, 1, totalPages)
    /** Объект с пустыми данными. */
    private val empty = object : ViewType { override fun getViewType() = ViewType.Value.EMPTY }

    init {
        // Заполняются все доступные Delegate Adapters
        delegateAdapters.put(ViewType.Value.PAGINATOR, PaginatorDelegateAdapter(eventHandler))
        delegateAdapters.put(ViewType.Value.USER, UserItemDelegateAdapter(eventHandler))
        delegateAdapters.put(ViewType.Value.EMPTY, EmptyDelegateAdapter())
    }

    /** Заменяет все данные в адаптере на новые.
     * Очищает адаптер, заполняет items из users, устанавливает пагинатор users.number и users.total
     *
     * @param users данные в формате страницы
     */
    fun replaceUsers(users: UsersPage) {
        dataSource.clear()
        paginator.page = users.number
        paginator.totalPages = users.total
        if (!users.items.isEmpty()) {
            dataSource.addAll(users.items)
            dataSource.add(paginator)
        } else {
            dataSource.add(empty)
        }
        notifyDataSetChanged()
    }

}