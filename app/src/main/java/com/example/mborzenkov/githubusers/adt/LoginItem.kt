package com.example.mborzenkov.githubusers.adt

import com.example.mborzenkov.githubusers.login.LoginManager.Companion.AuthType

/** Тип данных, представляющий пользователя приложения (логин).
 *
 * @param uid property [uid]
 * @param fullname property [fullname]
 * @param authType property [authType]
 * @param token property [token]
 * @param imageUrl property [imageUrl]
 *
 * @property uid Уникальный идентификатор пользователя.
 * @property fullname Полное имя пользователя.
 * @property authType Тип аутентификации, один из [AuthType].
 * @property token Токен, выданный при аутентификации.
 * @property imageUrl Ссылка на фотографию (локальная или внешняя).
 */
data class LoginItem(
        val uid: String,
        var fullname: String,
        @AuthType var authType: String,
        var token: String,
        var imageUrl: String
) {

    companion object {
        /** Создает новый логин с указанным uid и остальными полями пустыми.
         *
         * @param uid уникальный идентификатор пользователя
         */
        fun newLogin(uid: String): LoginItem {
            return LoginItem(uid, "", "", "", "")
        }

        @JvmField
        val EMPTY = LoginItem("", "", "", "", "")
    }

    /** Вычисляет хэш код логина.
     * Хэш код зависит только от uid.
     *
     * @return хэш код
     */
    override fun hashCode(): Int {
        return uid.hashCode()
    }

    /** Определяет равенство объектов.
     * Объекты LoginItem равны, если их uid равны.
     *
     * @param other объект для сравнения
     *
     * @return true, если other.uid == this.uid
     */
    override fun equals(other: Any?): Boolean {
         if (this === other) return true
         if (javaClass != other?.javaClass) return false

         other as LoginItem

         if (uid != other.uid) return false

         return true
     }

 }