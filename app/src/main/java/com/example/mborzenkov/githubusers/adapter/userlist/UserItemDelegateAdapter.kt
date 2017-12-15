package com.example.mborzenkov.githubusers.adapter.userlist

import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.ImageView
import com.example.mborzenkov.githubusers.R
import com.example.mborzenkov.githubusers.activity.MainActivity
import com.example.mborzenkov.githubusers.adapter.ViewType
import com.example.mborzenkov.githubusers.adapter.ViewTypeDelegateAdapter
import com.example.mborzenkov.githubusers.adt.UserItem
import com.example.mborzenkov.githubusers.commons.inflate
import com.example.mborzenkov.githubusers.commons.loadImg
import kotlinx.android.synthetic.main.content_userlist_item.view.*

/** DelegateAdapter для пользователя - элемента списка.
 *
 * @param eventHandler обработчик нажатий в списке
 */
class UserItemDelegateAdapter(
        val eventHandler: UserListAdapter.UserListAdapterEventHandler) : ViewTypeDelegateAdapter {

    override fun onCreateViewHolder(parent: ViewGroup) = ViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as ViewHolder
        holder.bind(item as UserItem)
    }

    /** ViewHolder для пользователя - элемента списка.
     *
     * @param parent ViewGroup в который нужно добавить новый View
     */
    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.content_userlist_item)) {

        private lateinit var itemImage: ImageView

        /** Выполняет заполнение view данными из item.
         *
         * @param item источник данных
         */
        fun bind (item: UserItem) = with(itemView) {
            tv_userlist_item_username.text = item.username
            iv_userlist_item_image.loadImg(item.imageUrl)
            itemImage = iv_userlist_item_image
            itemView.setOnClickListener( {
                ViewCompat.setTransitionName(iv_userlist_item_image, MainActivity.SHARED_ELEMENT_IMAGE_TRANSITION_NAME)
                eventHandler.onUserClick(item, iv_userlist_item_image)
            } )
        }

    }

}