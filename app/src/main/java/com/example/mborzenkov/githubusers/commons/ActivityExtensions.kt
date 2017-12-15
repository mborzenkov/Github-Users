@file:JvmName("ActivityExtensionsUtils")

package com.example.mborzenkov.githubusers.commons

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar

/* Расширения для Activity. */

/** Устанавливает новый [Toolbar].
 *
 * @param toolbar новый тулбар
 */
fun AppCompatActivity.setNewToolbar(toolbar: Toolbar) {
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(supportFragmentManager.backStackEntryCount > 1)
}

/** Устанавливает заголовок в AppBar.
 *
 * @param title заголовок appbar
 */
fun AppCompatActivity.setAppBarTitle(title: String) {
    supportActionBar?.title = title
}