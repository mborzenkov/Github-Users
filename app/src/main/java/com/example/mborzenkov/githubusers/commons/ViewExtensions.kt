@file:JvmName("ViewExtensionsUtils")

package com.example.mborzenkov.githubusers.commons

import android.support.annotation.LayoutRes
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.mborzenkov.githubusers.R
import com.squareup.picasso.Picasso

/* Расширения для View и ViewGroup. */

/** Выполняет inflate.
 *
 * @param layoutId идентфикатор layout
 * @param attachToRoot признак, присоединять ли к корню
 *
 * @return корень получившегося view
 */
fun ViewGroup.inflate(@LayoutRes layoutId: Int, attachToRoot: Boolean = false) : View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

/** Загружает картинку в [ImageView].
 * В случае, если imageUrl пустая, загружает заранее определенный плейсхолдер
 *
 * @param imageUrl ссылка на картинку, локальная или внешняя
 */
fun ImageView.loadImg(imageUrl: String) {
    if (!TextUtils.isEmpty(imageUrl)) {
        Picasso.with(context).load(imageUrl).into(this)
    } else {
        Picasso.with(context).load(R.mipmap.img_user_empty).into(this)
    }
}

/** Устанавливает видимость.
 *
 * @param visibility если true, устанавливает [View.VISIBLE], иначе [View.INVISIBLE]
 */
fun View.setVisibility(visibility: Boolean) {
    this.visibility = if (visibility) View.VISIBLE else View.INVISIBLE
}
