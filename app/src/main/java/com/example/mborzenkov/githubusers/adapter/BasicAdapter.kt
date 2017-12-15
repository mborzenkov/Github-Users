package com.example.mborzenkov.githubusers.adapter

import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

/** Базовый класс адаптера по шаблону Delegate Adapter. */
open class BasicAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /** Источник данных. */
    internal var dataSource: ArrayList<ViewType> = ArrayList()
    /** Все Delegate Adapters. */
    internal var delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegateAdapters.get(viewType).onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegateAdapters
                .get(getItemViewType(position))
                .onBindViewHolder(holder, dataSource[position])
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

    override fun getItemViewType(position: Int): Int {
        return this.dataSource[position].getViewType()
    }
}