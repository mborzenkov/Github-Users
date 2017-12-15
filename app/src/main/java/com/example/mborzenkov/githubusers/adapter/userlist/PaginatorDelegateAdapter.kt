package com.example.mborzenkov.githubusers.adapter.userlist

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.example.mborzenkov.githubusers.R
import com.example.mborzenkov.githubusers.adapter.userlist.UserListAdapter.UserListAdapterEventHandler
import com.example.mborzenkov.githubusers.adapter.ViewType
import com.example.mborzenkov.githubusers.adapter.ViewTypeDelegateAdapter
import com.example.mborzenkov.githubusers.adt.PaginatorItem
import com.example.mborzenkov.githubusers.commons.inflate
import com.example.mborzenkov.githubusers.commons.setVisibility
import kotlinx.android.synthetic.main.content_userlist_pagination.view.*

/** DelegateAdapter для пагинатора.
 *
 * @param eventHandler обработчик событий в пагинаторе
 */
class PaginatorDelegateAdapter(
        val eventHandler: UserListAdapterEventHandler) : ViewTypeDelegateAdapter {

    companion object {
        /** Текстовый формат пагинатора. */
        private const val PAGINATOR_FORMAT = "%s %s / %s"
    }

    override fun onCreateViewHolder(parent: ViewGroup) = ViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as ViewHolder
        holder.bind(item as PaginatorItem)
    }


    /** ViewHolder для пагинатора.
     *
     * @param parent ViewGroup в который нужно добавить новый View
     */
    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.content_userlist_pagination)) {

        /** Выполняет заполнение view данными из item.
         *
         * @param item источник данных
         */
        fun bind (item: PaginatorItem) = with(itemView) {
            tv_pagination_pages.text =
                    PAGINATOR_FORMAT.format(item.prefix, item.page, item.totalPages)
            button_pagination_prev.setVisibility(item.page > 1)
            button_pagination_next.setVisibility(item.page < item.totalPages)
            button_pagination_prev.setOnClickListener( { eventHandler.onPrevClick() })
            button_pagination_next.setOnClickListener( { eventHandler.onNextClick() })
        }

    }

}