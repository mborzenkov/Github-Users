@file:JvmName("FileExtensionsUtils")

package com.example.mborzenkov.githubusers.commons

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.squareup.picasso.Picasso
import java.io.File

/* Расширения для работы с файлами. */

/** Записывает строку в файл.
 *
 * @param content строка
 */
fun File.writeStringToFile(content: String) {
    this.bufferedWriter().use { it.write(content) }
}

/** Читает содержимое файла в строку.
 *
 * @return содержимое файла в виде строки или "", если не удалось прочитать содержимое
 */
fun File.readFileToString(): String {
    return if (this.exists()) this.bufferedReader().use { it.readText() } else ""
}

/** Выполняет сохранение картинки из сети в файл.
 *
 * @param context контекст
 * @param imageUrl ссылка на картинку
 * @param onSuccess колбек об успешном сохранении
 */
fun File.loadImageFromWeb(context: Context,
                     imageUrl: String,
                     onSuccess: (String) -> Unit) {
    Picasso
            .with(context)
            .load(imageUrl)
            .into(object : com.squareup.picasso.Target {
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                override fun onBitmapFailed(errorDrawable: Drawable?) {}
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    bitmap?.let {
                        val localFile = this@loadImageFromWeb
                        if (localFile.exists()) {
                            localFile.delete()
                        }
                        localFile.createNewFile()
                        localFile.outputStream().use {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, it)
                            onSuccess(localFile.toURI().toString())
                        }
                    }
                }
            })
}