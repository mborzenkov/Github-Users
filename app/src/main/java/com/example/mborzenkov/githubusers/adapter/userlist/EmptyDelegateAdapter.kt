package com.example.mborzenkov.githubusers.adapter.userlist

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.example.mborzenkov.githubusers.R
import com.example.mborzenkov.githubusers.adapter.ViewType
import com.example.mborzenkov.githubusers.adapter.ViewTypeDelegateAdapter
import com.example.mborzenkov.githubusers.commons.inflate

/** DelegateAdapter для пустого списка. */
class EmptyDelegateAdapter : ViewTypeDelegateAdapter {

    override fun onCreateViewHolder(parent: ViewGroup) = ViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) { }

    /** ViewHolder для пустого списка.
     *
     * @param parent ViewGroup в который нужно добавить новый View
     */
    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.content_userlist_empty))

}