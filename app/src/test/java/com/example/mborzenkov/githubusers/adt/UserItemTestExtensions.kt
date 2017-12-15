@file:JvmName("UserItemTestExtensionsUtil")

package com.example.mborzenkov.githubusers.adt

fun UserItem.trulyEquals(other: UserItem): Boolean {
    return this.username == other.username
            && this.fullname == other.fullname
            && this.location == other.location
            && this.email == other.email
            && this.imageUrl == other.imageUrl
            && this.profileUrl == other.profileUrl
            && this.bio == other.bio
            && this.followers == other.followers
            && this.blog == other.blog
}