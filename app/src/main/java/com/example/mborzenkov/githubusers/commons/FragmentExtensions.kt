@file:JvmName("FragmentExtensionsUtils")

package com.example.mborzenkov.githubusers.commons

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.widget.ImageView
import com.example.mborzenkov.githubusers.R

/* Расширения для фрагментов. */

/** Заменяет фрагмент на новый, добавляет в backstack.
 *
 * @param containerId контейнер, куда нужно положить фрагмент
 * @param fragment фрагмент, который нужно положить
 * @param tag тэг фрагмента
 * @param animate признак, использовать ли стандартную анимацию
 * @param sharedElement Shared Element для перемещения из фрагмента в фрагмент
 * @param sharedElementName имя Shared Element для выполнения перемещения
 */
fun FragmentManager.changeFragment(@IdRes containerId: Int,
                                   fragment: Fragment,
                                   tag: String,
                                   animate: Boolean = true,
                                   sharedElement: ImageView? = null,
                                   sharedElementName: String = "") {
    val transaction = this.beginTransaction()
    if (animate) {
        transaction
                .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out)
    }
    if (sharedElement != null) {
        transaction.addSharedElement(sharedElement, sharedElementName)
    }
    transaction
            .replace(containerId, fragment, tag)
            .addToBackStack(null)
            .commit()
}